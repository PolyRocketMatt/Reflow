package com.github.polyrocketmatt.reflow.handler;

import com.github.polyrocketmatt.reflow.asm.decompilation.asm.AsmDependencyDecompiler;
import com.github.polyrocketmatt.reflow.utils.ByteUtils;
import com.github.polyrocketmatt.reflow.asm.wrapper.ClassWrapper;
import com.google.common.io.ByteStreams;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClassHandler {

    public static final ClassHandler INSTANCE = new ClassHandler();

    private File file;
    private Map<ClassWrapper, String> classes;
    private Map<String, Set<ClassWrapper>> innerClassMap;
    private Map<String, byte[]> resources;
    private Logger logger;

    protected ClassHandler() {}

    public void init(File file) {
        this.file = file;
        this.classes = new HashMap<>();
        this.innerClassMap = new HashMap<>();
        this.resources = new HashMap<>();
        this.logger = LoggerFactory.getLogger("ClassHandler");

        try {
            run();
        } catch (Exception ex) {
            logger.error("Failed to load classes from JAR");
        }
    }

    private void run() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                byte[] data = ByteStreams.toByteArray(zis);

                String name = entry.getName();

                //  TODO: Handle, cascading inner classes
                if (name.endsWith(".class")) {
                    ClassNode classNode = ByteUtils.parseBytesToClassNode(data);
                    AsmDependencyDecompiler dependencyDecompiler = ByteUtils.parseDependencies(data);
                    Set<String> imports = dependencyDecompiler.getImports();
                    ClassWrapper wrapper = new ClassWrapper(classNode, imports, data, false);
                    boolean isInnerClass = classNode.name.contains("$");

                    //  If this class is an inner class, we don't want to add it to the list of classes
                    if (isInnerClass) {
                        String key = wrapper.getSimpleName().substring(0, wrapper.getSimpleName().indexOf("$"));
                        Set<ClassWrapper> wrappers = innerClassMap.getOrDefault(key, new HashSet<>());
                        wrappers.add(wrapper);
                        innerClassMap.put(key, wrappers);

                        continue;
                    }
                    classes.put(wrapper, name);
                } else {
                    if (name.equals("META-INF/MANIFEST.MF")) {
                        String manifest = new String(data);

                        // TODO: What does this do?
                        manifest = manifest.substring(0, manifest.length() - 2);

                        // Place a watermark
                        manifest += "Obfuscated-By: ReFlow-0.0.1\r\n";

                        data = manifest.getBytes();
                    }

                    resources.put(name, data);
                }
            }
        }

        logger.info("Loaded {} classes!", classes.size());

        //  Update inner classes
        for (ClassWrapper wrapper : classes.keySet()) {
            String key = wrapper.getSimpleName();
            Set<ClassWrapper> wrappers = innerClassMap.getOrDefault(key, new HashSet<>());
            Set<String> innerClassImports = wrappers.stream().map(ClassWrapper::getImports).reduce(new HashSet<>(), (a, b) -> {
                a.addAll(b);

                return a;
            });

            wrapper.updateImports(innerClassImports);
            wrapper.updateInnerClasses(wrappers);
        }
    }

    public Map<ClassWrapper, String> getClasses() {
        return classes;
    }

    public Map<String, Set<ClassWrapper>> getInnerClassMap() {
        return innerClassMap;
    }

    public Set<ClassWrapper> getInnerClasses(String key) {
        return innerClassMap.getOrDefault(key, new HashSet<>());
    }

}

package com.github.polyrocketmatt.reflow.decompiler;

import com.github.polyrocketmatt.reflow.decompiler.utils.ByteUtils;
import com.github.polyrocketmatt.reflow.decompiler.wrapper.ClassWrapper;
import com.github.polyrocketmatt.reflow.decompiler.wrapper.EntityWrapper;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ReflowJarHandler {

    private final File file;

    private final Map<String, byte[]> resources;
    private final Map<String, ClassWrapper> classWrappers;

    private ReflowJarHandler(File file) {
        this.file = file;
        this.resources = new HashMap<>();
        this.classWrappers = new HashMap<>();
    }

    public Map<String, byte[]> getResources() {
        return resources;
    }

    public Map<String, ClassWrapper> getClassWrappers() {
        return classWrappers;
    }

    public EntityWrapper parseClass(ZipInputStream zipInputStream, ZipEntry entry) throws IOException {
        String name = entry.getName();
        byte[] data = ByteUtils.readBytes(zipInputStream, entry);

        if (name.endsWith(".class")) {
            ClassNode classNode = ByteUtils.parseBytesToClassNode(data);
            ClassWrapper classWrapper = new ClassWrapper(classNode);

            classWrappers.put(classWrapper.getSimpleName(), classWrapper);

            /*
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
                        wrapperMap.put(wrapper.getSimpleName(), wrapper);

                        continue;
                    }

                    classes.put(wrapper, name);
                    wrapperMap.put(wrapper.getSimpleName(), wrapper);

                     */
            return classWrapper;
        } else {
            if (name.equals("META-INF/MANIFEST.MF")) {
                String manifest = new String(data);

                // Place a watermark
                manifest = manifest.substring(0, manifest.length() - 2);
                manifest += "Processed-With: ReFlow-0.0.1\r\n";
                data = manifest.getBytes();
            }

            resources.put(name, data);

            return null;
        }
    }

    public static ReflowJarHandler of(File file) {
        return new ReflowJarHandler(file);
    }

}

package com.github.polyrocketmatt.flowfuscate.handler;

import com.github.polyrocketmatt.flowfuscate.utils.ByteUtils;
import com.github.polyrocketmatt.flowfuscate.wrapper.ClassWrapper;
import com.google.common.io.ByteStreams;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClassHandler {

    private final File file;
    private final Map<ClassWrapper, String> classes;
    private final Map<String, byte[]> resources;
    private final Logger logger;

    public ClassHandler(File file) {
        this.file = file;
        this.classes = new HashMap<>();
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

                if (name.endsWith(".class")) {
                    ClassNode classNode = ByteUtils.parseBytesToClassNode(data);

                    classes.put(new ClassWrapper(classNode, false), name);
                } else {
                    if (name.equals("META-INF/MANIFEST.MF")) {
                        String manifest = new String(data);

                        // TODO: What does this do?
                        manifest = manifest.substring(0, manifest.length() - 2);

                        // Place a watermark
                        manifest += "Obfuscated-By: FlowFuscate-0.0.1\r\n";

                        data = manifest.getBytes();
                    }

                    resources.put(name, data);
                }
            }
        }

        logger.info("Loaded {} classes!", classes.size());
    }

    public Map<ClassWrapper, String> getClasses() {
        return classes;
    }
}

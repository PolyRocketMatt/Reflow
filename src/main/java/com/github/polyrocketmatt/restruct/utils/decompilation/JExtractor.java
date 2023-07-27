package com.github.polyrocketmatt.restruct.utils.decompilation;

import com.github.polyrocketmatt.restruct.asm.wrapper.ClassWrapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JExtractor {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void extractClasses(File jarFile, File tempDir, Set<ClassWrapper> wrappers) throws IOException {
        if (!tempDir.exists())
            tempDir.mkdirs();

        try (JarFile jar = new JarFile(jarFile)) {
            for (ClassWrapper wrapper : wrappers) {
                JarEntry entry = jar.getJarEntry(wrapper.getClassName() + ".class");

                if (entry != null && entry.getName().endsWith(".class")) {
                    Path dest = tempDir.toPath().resolve(entry.getName());
                    Files.createDirectories(dest.getParent());

                    try (InputStream is = jar.getInputStream(entry)) {
                        Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
    }

}

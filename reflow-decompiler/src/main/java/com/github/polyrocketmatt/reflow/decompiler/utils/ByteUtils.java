package com.github.polyrocketmatt.reflow.decompiler.utils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ByteUtils {

    public static byte[] readBytes(ZipInputStream zis, ZipEntry entry) throws IOException {
        //  Buffer to read bytes into
        byte[] buffer = new byte[1024];

        //  Byte output stream to write bytes to
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //  Read bytes from the zip input stream
        int bytesRead;
        while ((bytesRead = zis.read(buffer)) != -1)
            baos.write(buffer, 0, bytesRead);

        //  Close streams
        zis.closeEntry();
        baos.close();

        //  Return the bytes
        return baos.toByteArray();
    }

    public static byte[] readBytes(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }

    public static byte[] readBytes(InputStream is) throws IOException {
        //  Buffer to read bytes into
        byte[] buffer = new byte[1024];

        //  Byte output stream to write bytes to
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //  Read bytes from the input stream
        int bytesRead;
        while ((bytesRead = is.read(buffer, 0, buffer.length)) != -1)
            baos.write(buffer, 0, bytesRead);

        //  Close the streams
        baos.close();
        is.close();

        //  Return the bytes
        return baos.toByteArray();
    }

    public static ClassNode parseBytesToClassNode(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();

        reader.accept(node, 0);

        return node;
    }

}

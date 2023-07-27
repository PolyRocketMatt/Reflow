package com.github.polyrocketmatt.restruct.utils;

import com.github.polyrocketmatt.restruct.asm.decompilation.asm.AsmDependencyDecompiler;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class ByteUtils {

    public static ClassNode parseBytesToClassNode(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();

        reader.accept(node, 0);

        return node;
    }

    public static AsmDependencyDecompiler parseDependencies(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        AsmDependencyDecompiler decompiler = new AsmDependencyDecompiler();

        reader.accept(decompiler, 0);

        return decompiler;
    }

    public static byte[] parseClassNodeToBytes(ClassNode node) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        node.accept(writer);

        return writer.toByteArray();
    }

    public static String byteArrayToString(byte[] bytes) {
        return new String(bytes);
    }

    public static boolean bytesMatch(byte[] first, byte[] second) {
        if (first.length != second.length)
            return false;

        for (int i = 0; i < first.length; i++)
            if (first[i] != second[i])
                return false;
        return true;
    }

}

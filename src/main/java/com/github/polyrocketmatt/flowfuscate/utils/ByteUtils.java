package com.github.polyrocketmatt.flowfuscate.utils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class ByteUtils {

    public static ClassNode parseBytesToClassNode(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        ClassNode node = new ClassNode();

        reader.accept(node, 0);

        return node;
    }

    public static byte[] patseClassNodeToBytes(ClassNode node) {
        return null;
    }

}

package com.github.polyrocketmatt.reflow.utils;

import org.objectweb.asm.Opcodes;

public class AccessUtils {

    public static boolean isPublic(int access) {
        return (access & Opcodes.ACC_PUBLIC) != 0;
    }

    public static boolean isPrivate(int access) {
        return (access & Opcodes.ACC_PRIVATE) != 0;
    }

    public static boolean isProtected(int access) {
        return (access & Opcodes.ACC_PROTECTED) != 0;
    }

    public static boolean isStatic(int access) {
        return (access & Opcodes.ACC_STATIC) != 0;
    }

    public static boolean isAbstract(int access) {
        return (access & Opcodes.ACC_ABSTRACT) != 0 && !isInterface(access) && !isAnnotation(access);
    }

    public static boolean isFinal(int access) {
        return (access & Opcodes.ACC_FINAL) != 0;
    }

    public static boolean isSynchronized(int access) {
        return (access & Opcodes.ACC_SYNCHRONIZED) != 0;
    }

    public static boolean isNative(int access) {
        return (access & Opcodes.ACC_NATIVE) != 0;
    }

    public static boolean isStrict(int access) {
        return (access & Opcodes.ACC_STRICT) != 0;
    }

    public static boolean isBridge(int access) {
        return (access & Opcodes.ACC_BRIDGE) != 0;
    }

    public static boolean isVarArgs(int access) {
        return (access & Opcodes.ACC_VARARGS) != 0;
    }

    public static boolean isSynthetic(int access) {
        return (access & Opcodes.ACC_SYNTHETIC) != 0;
    }

    public static boolean isMandated(int access) {
        return (access & Opcodes.ACC_MANDATED) != 0;
    }

    public static boolean isInterface(int access) {
        return (access & Opcodes.ACC_INTERFACE) != 0;
    }

    public static boolean isAnnotation(int access) {
        return (access & Opcodes.ACC_ANNOTATION) != 0;
    }

    public static boolean isEnum(int access) {
        return (access & Opcodes.ACC_ENUM) != 0;
    }

    public static boolean isModule(int access) {
        return (access & Opcodes.ACC_MODULE) != 0;
    }

    public static String getAccess(int access) {
        if (isPublic(access))
            return "public";
        else if (isPrivate(access))
            return "private";
        else if (isProtected(access))
            return "protected";
        return "";
    }

    public static String getClassType(int access) {
        if (isAnnotation(access))
            return "@interface";
        else if (isInterface(access))
            return "interface";
        else if (isEnum(access))
            return "enum";
        else if (isModule(access))
            return "module";
        return "class";
    }


}

package com.github.polyrocketmatt.reflow.utils.types;

import com.github.polyrocketmatt.reflow.wrapper.ClassWrapper;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum JavaLangTypes implements BuiltInTypes {

    //  java.lang
    APPENDABLE("Appendable"),
    AUTO_CLOSEABLE("AutoCloseable"),
    CHAR_SEQUENCE("CharSequence"),
    CLONEABLE("Cloneable"),
    COMPARABLE("Comparable"),
    ITERABLE("Iterable"),
    READABLE("Readable"),
    RUNNABLE("Runnable"),
    THREAD_UNCAUGHT_EXCEPTION_HANDLER("Thread.UncaughtExceptionHandler"),

    BOOLEAN("Boolean"),
    BYTE("Byte"),
    CHAR("Character"),
    CHAR_SUBSET("Character.Subset"),
    CHAR_UNICODE_BLOCK("Character.UnicodeBlock"),
    CLASS("Class"),
    CLASS_LOADER("ClassLoader"),
    CLASS_VALUE("ClassValue"),
    COMPILER("Compiler"),
    DOUBLE("Double"),
    ENUM("Enum"),
    FLOAT("Float"),
    INHERITABLE_THREAD_LOCAL("InheritableThreadLocal"),
    INTEGER("Integer"),
    LONG("Long"),
    MATH("Math"),
    NUMBER("Number"),
    OBJECT("Object"),
    PACKAGE("Package"),
    PROCESS("Process"),
    PROCESS_BUILDER("ProcessBuilder"),
    PROCESS_BUILDER_REDIRECT("ProcessBuilder.Redirect"),
    RUNTIME("Runtime"),
    RUNTIME_PERMISSION("RuntimePermission"),
    SECURITY_MANAGER("SecurityManager"),
    SHORT("Short"),
    STACK_TRACE_ELEMENT("StackTraceElement"),
    STRICT_MATH("StrictMath"),
    STRING("String"),
    STRING_BUFFER("StringBuffer"),
    STRING_BUILDER("StringBuilder"),
    SYSTEM("System"),
    THREAD("Thread"),
    THREAD_GROUP("ThreadGroup"),
    THREAD_LOCAL("ThreadLocal"),
    THROWABLE("Throwable"),
    VOID("Void");


    private final String name;
    private final ClassWrapper wrapper;

    JavaLangTypes(String name) {
        this.name = name;
        this.wrapper = new ClassWrapper(name, true);
    }

    public String getName() {
        return name;
    }

    public ClassWrapper getWrapper() {
        return wrapper;
    }

    public static Set<ClassWrapper> asWrappers() {
        return Arrays.stream(values()).map(JavaLangTypes::getWrapper).collect(Collectors.toSet());
    }
}

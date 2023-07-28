package com.github.polyrocketmatt.reflow.decompiler.decompilers;

@FunctionalInterface
public interface ReflowDecompiler {

    void decompile(byte[] data);

}

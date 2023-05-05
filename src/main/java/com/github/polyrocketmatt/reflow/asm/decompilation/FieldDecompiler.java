package com.github.polyrocketmatt.reflow.asm.decompilation;

import org.objectweb.asm.FieldVisitor;

public class FieldDecompiler extends FieldVisitor {

    private final DependencyDecompiler decompiler;

    public FieldDecompiler(int api, DependencyDecompiler decompiler) {
        super(api);
        this.decompiler = decompiler;
    }
}

package com.github.polyrocketmatt.reflow.asm.decompilation;

import org.objectweb.asm.AnnotationVisitor;

public class AnnotationDecompiler extends AnnotationVisitor {

    private final DependencyDecompiler decompiler;

    public AnnotationDecompiler(int api, DependencyDecompiler decompiler) {
        super(api);
        this.decompiler = decompiler;
    }
}

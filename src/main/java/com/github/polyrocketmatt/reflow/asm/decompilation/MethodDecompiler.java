package com.github.polyrocketmatt.reflow.asm.decompilation;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class MethodDecompiler extends MethodVisitor {

    private final DependencyDecompiler decompiler;
    private final AnnotationVisitor annotationVisitor;

    public MethodDecompiler(int api, DependencyDecompiler decompiler) {
        super(api);
        this.decompiler = decompiler;
        this.annotationVisitor = new AnnotationDecompiler(api, decompiler);
    }

    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        decompiler.addDesc(desc);
        return annotationVisitor;
    }

    /**
     * Visits a type instruction
     * NEW, ANEWARRAY, CHECKCAST or INSTANCEOF.
     */
    public void visitTypeInsn(int opcode, String desc) {
        if (desc.charAt(0)=='[')
            decompiler.addDesc(desc);
        else
            decompiler.addName(desc);
    }

    /**
     * Visits a field instruction
     * GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
     */
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        decompiler.addName(owner);
        decompiler.addDesc(desc);
    }

    /**
     * Visits a method instruction
     * INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
     */
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        decompiler.addName(owner);
        decompiler.addMethodDesc(desc);
    }

    /**
     * Visits a LDC instruction.
     */
    public void visitLdcInsn(Object cst) {
        if (cst instanceof Type)
            decompiler.addType((Type) cst);
    }

    /**
     * Visits a MULTIANEWARRAY instruction.
     */
    public void visitMultiANewArrayInsn(String desc, int dims) {
        decompiler.addDesc(desc);
    }

    /**
     * Visits a try catch block.
     */
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        decompiler.addName(type);
    }


}

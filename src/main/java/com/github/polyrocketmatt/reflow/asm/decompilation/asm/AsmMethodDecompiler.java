package com.github.polyrocketmatt.reflow.asm.decompilation.asm;

import org.objectweb.asm.MethodVisitor;

import java.util.Stack;

import static com.github.polyrocketmatt.reflow.gui.FlowConstants.ASM_VERSION;

public class AsmMethodDecompiler extends MethodVisitor {

    private final Stack<String> instructionStack;

    public AsmMethodDecompiler() {
        super(ASM_VERSION);
        this.instructionStack = new Stack<>();
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }
}

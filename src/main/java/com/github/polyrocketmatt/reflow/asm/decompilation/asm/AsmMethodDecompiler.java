package com.github.polyrocketmatt.reflow.asm.decompilation.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.github.polyrocketmatt.reflow.gui.FlowConstants.ASM_VERSION;

public class AsmMethodDecompiler extends MethodVisitor {

    private final Stack<String> instructionStack;
    private final List<LocalVariableNode> localVariables;

    public AsmMethodDecompiler() {
        super(ASM_VERSION);
        this.instructionStack = new Stack<>();
        this.localVariables = new ArrayList<>();
    }

    public Stack<String> getInstructionStack() {
        return instructionStack;
    }

    public List<LocalVariableNode> getLocalVariables() {
        return localVariables;
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        System.out.println("FIELD INSTRUCTION: " + opcode + " " + owner + " " + name + " " + descriptor);

        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitInsn(int opcode) {
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        LabelNode startNode = new LabelNode(start);
        LabelNode endNode = new LabelNode(end);
        LocalVariableNode localVariableNode = new LocalVariableNode(name, descriptor, signature, startNode, endNode, index);
        localVariables.add(localVariableNode);

        super.visitLocalVariable(name, descriptor, signature, start, end, index);
    }

}

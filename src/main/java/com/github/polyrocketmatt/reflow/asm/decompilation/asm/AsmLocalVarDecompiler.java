package com.github.polyrocketmatt.reflow.asm.decompilation.asm;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;

import java.util.ArrayList;
import java.util.List;

import static com.github.polyrocketmatt.reflow.gui.FlowConstants.ASM_VERSION;

public class AsmLocalVarDecompiler extends MethodVisitor {

    private final List<LocalVariableNode> localVariables;

    public AsmLocalVarDecompiler() {
        super(ASM_VERSION);
        this.localVariables = new ArrayList<>();
    }

    public List<LocalVariableNode> getLocalVariables() {
        return localVariables;
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

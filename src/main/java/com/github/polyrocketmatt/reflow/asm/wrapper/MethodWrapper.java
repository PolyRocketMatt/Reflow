package com.github.polyrocketmatt.reflow.asm.wrapper;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.commons.CodeSizeEvaluator;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public class MethodWrapper implements Wrapper {

    private final @NotNull MethodNode node;
    private final int size;

    public MethodWrapper(@NotNull MethodNode node) {
        CodeSizeEvaluator evaluator = new CodeSizeEvaluator(null);

        node.accept(evaluator);

        this.node = node;
        this.size = evaluator.getMaxSize();
    }

    public @NotNull MethodNode getNode() {
        return node;
    }

    public int getSize() {
        return size;
    }

    public boolean hasInstructions() {
        return node.instructions != null && node.instructions.size() > 0;
    }

    public @NotNull InsnList getMethodInstructions() {
        if (!hasInstructions())
            throw new IllegalStateException("Method has no instructions");
        return node.instructions;
    }

}

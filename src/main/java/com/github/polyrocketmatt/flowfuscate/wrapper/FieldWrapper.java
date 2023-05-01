package com.github.polyrocketmatt.flowfuscate.wrapper;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.FieldNode;

public class FieldWrapper implements Wrapper {

    private final @NotNull FieldNode node;

    public FieldWrapper(@NotNull FieldNode node) {
        this.node = node;
    }

    public @NotNull FieldNode getNode() {
        return node;
    }
}

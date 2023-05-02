package com.github.polyrocketmatt.reflow.wrapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;

import java.util.Set;
import java.util.stream.Collectors;

public class ClassWrapper implements Wrapper {

    private final ClassNode node;
    private final Set<MethodWrapper> methodWrappers;
    private final Set<FieldWrapper> fieldWrappers;

    private final String className;
    private final String simpleName;
    private final boolean isLibraryNode;

    public ClassWrapper(@NotNull ClassNode node, boolean isLibraryNode) {
        this.node = node;
        this.methodWrappers = node.methods
                .stream()
                .map(MethodWrapper::new)
                .collect(Collectors.toSet());
        this.fieldWrappers = node.fields
                .stream()
                .map(FieldWrapper::new)
                .collect(Collectors.toSet());
        this.isLibraryNode = isLibraryNode;
        this.className = node.name;

        String[] split = className.split("/");
        this.simpleName = split[split.length - 1];
    }

    public ClassWrapper(String name, boolean isLibraryNode) {
        this.node = null;
        this.methodWrappers = null;
        this.fieldWrappers = null;
        this.isLibraryNode = isLibraryNode;
        this.className = name;
        this.simpleName = name;
    }

    public ClassNode getNode() {
        return node;
    }

    public Set<MethodWrapper> getMethodWrappers() {
        return methodWrappers;
    }

    public Set<FieldWrapper> getFieldWrappers() {
        return fieldWrappers;
    }

    public String getClassName() {
        return className;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public boolean isLibraryNode() {
        return isLibraryNode;
    }
}

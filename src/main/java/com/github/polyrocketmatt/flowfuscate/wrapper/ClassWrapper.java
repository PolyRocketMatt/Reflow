package com.github.polyrocketmatt.flowfuscate.wrapper;

import org.objectweb.asm.tree.ClassNode;

import java.util.Set;
import java.util.stream.Collectors;

public class ClassWrapper implements Wrapper {

    private final ClassNode node;
    private final Set<MethodWrapper> methodWrappers;
    private final Set<FieldWrapper> fieldWrappers;

    private final String className;
    private final boolean isLibraryNode;

    public ClassWrapper(ClassNode node, boolean isLibraryNode) {
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

    public boolean isLibraryNode() {
        return isLibraryNode;
    }
}

package com.github.polyrocketmatt.reflow.asm.wrapper;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import java.util.Set;
import java.util.stream.Collectors;

public class ClassWrapper implements Wrapper {

    private final ClassNode node;
    private final Set<String> imports;
    private final Set<MethodWrapper> methodWrappers;
    private final Set<FieldWrapper> fieldWrappers;

    private final String className;
    private final String simpleName;
    private final byte[] data;
    private final boolean isLibraryNode;

    public ClassWrapper(@NotNull ClassNode node, @NotNull Set<String> imports, byte[] data, boolean isLibraryNode) {
        this.node = node;
        this.imports = imports;
        this.methodWrappers = node.methods
                .stream()
                .map(MethodWrapper::new)
                .collect(Collectors.toSet());
        this.fieldWrappers = node.fields
                .stream()
                .map(FieldWrapper::new)
                .collect(Collectors.toSet());
        this.data = data;
        this.isLibraryNode = isLibraryNode;
        this.className = node.name;

        String[] split = className.split("/");
        this.simpleName = split[split.length - 1];
    }

    public ClassWrapper(@NotNull String name, @NotNull Set<String> imports, byte[] data, boolean isLibraryNode) {
        this.node = null;
        this.imports = imports;
        this.methodWrappers = null;
        this.fieldWrappers = null;
        this.data = data;
        this.isLibraryNode = isLibraryNode;
        this.className = name;
        this.simpleName = name;
    }

    public ClassNode getNode() {
        return node;
    }

    public Set<String> getImports() {
        return imports;
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

    public byte[] getData() {
        return data;
    }

    public boolean isLibraryNode() {
        return isLibraryNode;
    }

}

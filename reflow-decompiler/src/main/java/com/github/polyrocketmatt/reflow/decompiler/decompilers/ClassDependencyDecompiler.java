package com.github.polyrocketmatt.reflow.decompiler.decompilers;

import com.github.polyrocketmatt.reflow.decompiler.utils.AsmUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import java.util.ArrayList;
import java.util.List;

public class ClassDependencyDecompiler extends ClassVisitor implements ReflowDecompiler {

    private final List<String> packages;
    private final List<String> imports;

    public ClassDependencyDecompiler(byte[] bytes) {
        super(AsmUtils.ASM_VERSION);

        this.packages = new ArrayList<>();
        this.imports = new ArrayList<>();

    }

    @Override
    public void decompile(byte[] data) {
        ClassReader reader = new ClassReader(data);
        reader.accept(this, 0);
    }

    public List<String> getPackages() {
        return packages;
    }

    public List<String> getImports() {
        return imports;
    }

}

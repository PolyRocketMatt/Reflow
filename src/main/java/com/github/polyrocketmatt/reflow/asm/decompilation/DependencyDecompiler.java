package com.github.polyrocketmatt.reflow.asm.decompilation;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.objectweb.asm.Opcodes.ASM9;

public class DependencyDecompiler extends ClassVisitor implements Decompiler {

    private final Set<String> packages;
    private final Set<String> imports;
    private final HashMap<String, Integer> occurrences;

    //  Decompilers
    private final SignatureDecompiler signatureVisitor;
    private final FieldDecompiler fieldVisitor;
    private final MethodDecompiler methodVisitor;
    private final AnnotationDecompiler annotationVisitor;

    public DependencyDecompiler() {
        super(ASM9);

        this.packages = new HashSet<>();
        this.imports = new HashSet<>();
        this.occurrences = new HashMap<>();
        this.signatureVisitor = new SignatureDecompiler(ASM9);
        this.fieldVisitor = new FieldDecompiler(ASM9, this);
        this.methodVisitor = new MethodDecompiler(ASM9, this);
        this.annotationVisitor = new AnnotationDecompiler(ASM9, this);
    }

    public Set<String> getPackages() {
        return packages;
    }

    public Set<String> getImports() {
        return imports;
    }

    public HashMap<String, Integer> getOccurrences() {
        return occurrences;
    }

    public String getGroupKey(String name) {
        int n = name.lastIndexOf('/');
        if (n > -1)
            name = name.substring(0, n);
        packages.add(name);

        return name;
    }

    public void addName(String name) {
        if (name == null)
            return;
        String p = getGroupKey(name);

        if (occurrences.containsKey(p))
            occurrences.put(p, occurrences.get(p) + 1);
        else
            occurrences.put(p, 1);

        imports.add(name.replace('/', '.'));
    }

    public void addNames(String[] names) {
        if (names == null)
            return;
        for (String name : names)
            addName(name);
    }

    public void addDesc(String desc) {
        addType(Type.getType(desc));
    }

    public void addType(Type t) {
        switch (t.getSort()) {
            case Type.ARRAY -> addType(t.getElementType());
            case Type.OBJECT -> addName(t.getClassName().replace('.', '/'));
        }
    }

    public void addMethodDesc(String desc) {
        addType(Type.getReturnType(desc));

        Type[] types = Type.getArgumentTypes(desc);
        for (Type type : types)
            addType(type);
    }

    public void addSignature(String sign) {
        signatureVisitor.addSignature(sign);
    }

    public void addTypeSignature(String sign) {
        signatureVisitor.addTypeSignature(sign);
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        /*
        String pckg = decompiler.getGroupKey(name);
        HashMap<String, Integer> current = decompiler.getOccurrences();
        if(current == null) {
            current = new HashMap<String,Integer>();
            groups.put(pckg, current);
        }

         */

        if (signature == null) {
            addName(superName);
            addNames(interfaces);
        } else {
            addSignature(signature);
        }
    }

    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (signature == null)
            addDesc(desc);
        else
            addTypeSignature(signature);


        if(value instanceof Type)
            addType((Type) value);
        return fieldVisitor;
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (signature == null)
            addMethodDesc(desc);
        else
            addSignature(signature);
        addNames(exceptions);
        return methodVisitor;
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        addDesc(desc);
        return annotationVisitor;
    }

}
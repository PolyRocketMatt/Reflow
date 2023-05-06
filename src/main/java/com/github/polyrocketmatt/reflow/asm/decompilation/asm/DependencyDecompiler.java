package com.github.polyrocketmatt.reflow.asm.decompilation.asm;

import com.github.polyrocketmatt.reflow.asm.decompilation.Decompiler;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.objectweb.asm.Opcodes.ASM9;

public class DependencyDecompiler extends ClassVisitor implements Decompiler {

    private final Set<String> packages;
    private final Set<String> imports;
    private final HashMap<String, Integer> occurrences;

    //  Decompilers
    private final MethodDecompiler methodVisitor;
    private final SignatureDecompiler signatureVisitor;
    private final FieldDecompiler fieldVisitor;
    private final AnnotationDecompiler annotationVisitor;

    public DependencyDecompiler() {
        super(ASM9);

        this.packages = new HashSet<>();
        this.imports = new HashSet<>();
        this.occurrences = new HashMap<>();
        this.methodVisitor = new MethodDecompiler(ASM9, this);
        this.signatureVisitor = new SignatureDecompiler(ASM9);
        this.fieldVisitor = new FieldDecompiler(ASM9);
        this.annotationVisitor = new AnnotationDecompiler(ASM9);
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

    private static class AnnotationDecompiler extends AnnotationVisitor {

        public AnnotationDecompiler(int api) {
            super(api);
        }

        @Override
        public void visit(String name, Object value) {
            super.visit(name, value);
        }

        @Override
        public void visitEnum(String name, String descriptor, String value) {
            super.visitEnum(name, descriptor, value);
        }
    }

    private static class MethodDecompiler extends MethodVisitor {

        private final DependencyDecompiler decompiler;
        private final AnnotationVisitor annotationVisitor;

        public MethodDecompiler(int api, DependencyDecompiler decompiler) {
            super(api);
            this.decompiler = decompiler;
            this.annotationVisitor = new AnnotationDecompiler(api);
        }

        public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
            decompiler.addDesc(desc);
            return annotationVisitor;
        }

        /**
         * Visits a type instruction
         * NEW, ANEWARRAY, CHECKCAST or INSTANCEOF.
         */
        public void visitTypeInsn(int opcode, String desc) {
            if (desc.charAt(0)=='[')
                decompiler.addDesc(desc);
            else
                decompiler.addName(desc);
        }

        /**
         * Visits a field instruction
         * GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
         */
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            decompiler.addName(owner);
            decompiler.addDesc(desc);
        }

        /**
         * Visits a method instruction
         * INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
         */
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            decompiler.addName(owner);
            decompiler.addMethodDesc(desc);
        }

        /**
         * Visits a LDC instruction.
         */
        public void visitLdcInsn(Object cst) {
            if (cst instanceof Type)
                decompiler.addType((Type) cst);
        }

        /**
         * Visits a MULTIANEWARRAY instruction.
         */
        public void visitMultiANewArrayInsn(String desc, int dims) {
            decompiler.addDesc(desc);
        }

        /**
         * Visits a try catch block.
         */
        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            decompiler.addName(type);
        }

    }

    private static class FieldDecompiler extends FieldVisitor {

        public FieldDecompiler(int api) {
            super(api);
        }

    }

    private static class SignatureDecompiler extends SignatureVisitor {

        public SignatureDecompiler(int api) {
            super(api);
        }

        public void addSignature(String sign) {
            if (sign != null) {
                new SignatureReader(sign).accept(this);
            }
        }

        public void addTypeSignature(String sign) {
            if (sign!=null) {
                new SignatureReader(sign).acceptType(this);
            }
        }

    }

}
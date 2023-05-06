package com.github.polyrocketmatt.reflow.asm.decompilation.visitor;

import com.github.polyrocketmatt.reflow.asm.wrapper.ClassWrapper;
import com.github.polyrocketmatt.reflow.gui.component.FlowStylePane;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.Set;

public class ClassFileDecompiler extends ClassVisitor {

    private final String source;
    private final ClassWrapper wrapper;
    private final FlowStylePane pane;

    private int access;

    public ClassFileDecompiler(String source, ClassWrapper wrapper, FlowStylePane pane) {
        super(Opcodes.ASM9);

        this.source = source;
        this.wrapper = wrapper;
        this.pane = pane;

        decompile();
    }

    public void decompile() {
        ClassReader reader = new ClassReader(wrapper.getData());

        reader.accept(this, 0);

        //  We first parse the access and modifier flags
        String accessModifier = getAccess();
        String staticModifier = isStatic() ? "static" : "";
        String finalModifier = isFinal() ? "final" : "";
        String abstractModifier = isAbstract() ? "abstract" : "";
        String classType = getClassType();

        boolean isAnnotation = isAnnotation();

        //  Then we parse the class name
        String className = wrapper.getSimpleName();

        //  Then we parse the super class
        String superClassName = wrapper.getNode().superName;
        if (superClassName != null && superClassName.equals("java/lang/Object"))
            superClassName = null;
        else if (superClassName != null)
            superClassName = superClassName.replace('/', '.');

        //  Then we parse the interfaces
        List<String> interfaces = wrapper.getNode().interfaces
                .stream()
                .map(s -> s.replace('/', '.'))
                .map(s -> s.substring(s.lastIndexOf('.') + 1))
                .filter(s -> isAnnotation && !s.equals("Annotation"))
                .toList();

        //  Then we parse the class signature
        String signature = wrapper.getNode().signature;

        //  Then we parse the class annotations

        //  Finally, we insert everything into the pane
        //      Insert access modifier
        pane.insert(accessModifier, pane.getKeywordStyle());
        pane.insert(accessModifier.isBlank() ? "" : " ", null);

        //      Insert static modifier
        pane.insert(staticModifier, pane.getKeywordStyle());
        pane.insert(staticModifier.isBlank() ? "" : " ", null);

        //      Insert final modifier
        pane.insert(finalModifier, pane.getKeywordStyle());
        pane.insert(finalModifier.isBlank() ? "" : " ", null);

        //      Insert abstract modifier
        pane.insert(abstractModifier, pane.getKeywordStyle());
        pane.insert(abstractModifier.isBlank() ? "" : " ", null);

        //      Insert class type
        pane.insert(classType, isAnnotation ? pane.getAnnotationStyle() : pane.getKeywordStyle());
        pane.insert(" ", null);

        //      Insert class name
        pane.insert(className, pane.getTypeStyle());
        pane.insert(" ", null);

        //      Insert super class
        if (superClassName != null) {
            pane.insert("extends", pane.getKeywordStyle());
            pane.insert(" ", null);
            pane.insert(superClassName, pane.getTypeStyle());
            pane.insert(" ", null);
        }

        //      Insert interfaces
        if (!interfaces.isEmpty()) {
            pane.insert("implements", pane.getKeywordStyle());
            pane.insert(" ", null);
            for (int i = 0; i < interfaces.size(); i++) {
                pane.insert(interfaces.get(i), pane.getTypeStyle());
                pane.insert(i == interfaces.size() - 1 ? " " : ", ", null);
            }
        }

        //      Insert class opening brace
        pane.insert("{\n\n", null);

        //      Decompile in order:
        //      1. Fields
        //      2. Methods
        //      3. Inner classes

        //      5. End brace
        pane.insert("}", null);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.access = access;
    }

    public String getAccess() {
        if (isPublic())
            return "public";
        else if (isPrivate())
            return "private";
        else if (isProtected())
            return "protected";
        return "";
    }

    public String getClassType() {
        if (isAnnotation())
            return "@interface";
        else if (isInterface())
            return "interface";
        else if (isEnum())
            return "enum";
        else if (isModule())
            return "module";
        return "class";
    }

    public boolean isPublic() {
        return (access & Opcodes.ACC_PUBLIC) != 0;
    }

    public boolean isPrivate() {
        return (access & Opcodes.ACC_PRIVATE) != 0;
    }

    public boolean isProtected() {
        return (access & Opcodes.ACC_PROTECTED) != 0;
    }

    public boolean isStatic() {
        return (access & Opcodes.ACC_STATIC) != 0;
    }

    public boolean isAbstract() {
        return (access & Opcodes.ACC_ABSTRACT) != 0 && !isInterface() && !isAnnotation();
    }

    public boolean isFinal() {
        return (access & Opcodes.ACC_FINAL) != 0;
    }

    public boolean isInterface() {
        return (access & Opcodes.ACC_INTERFACE) != 0;
    }

    public boolean isAnnotation() {
        return (access & Opcodes.ACC_ANNOTATION) != 0;
    }

    public boolean isEnum() {
        return (access & Opcodes.ACC_ENUM) != 0;
    }

    public boolean isModule() {
        return (access & Opcodes.ACC_MODULE) != 0;
    }

}

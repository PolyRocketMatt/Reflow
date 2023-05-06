package com.github.polyrocketmatt.reflow.asm.decompilation.custom;

import com.github.polyrocketmatt.reflow.asm.decompilation.asm.AsmAnnotationDecompiler;
import com.github.polyrocketmatt.reflow.asm.wrapper.ClassWrapper;
import com.github.polyrocketmatt.reflow.gui.component.FlowStylePane;
import com.github.polyrocketmatt.reflow.utils.types.Pair;
import org.checkerframework.checker.units.qual.A;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.ArrayList;
import java.util.List;

public class ClassFileDecompiler extends ClassVisitor {

    private final String source;
    private final ClassWrapper wrapper;
    private final FlowStylePane pane;
    private final String offset;

    private final AsmAnnotationDecompiler annotationDecompiler = new AsmAnnotationDecompiler();

    private int access;

    public ClassFileDecompiler(String source, ClassWrapper wrapper, FlowStylePane pane, String offset) {
        super(Opcodes.ASM9);

        this.source = source;
        this.wrapper = wrapper;
        this.pane = pane;
        this.offset = offset;

        decompile();
    }

    public void decompile() {
        ClassReader reader = new ClassReader(wrapper.getData());

        reader.accept(this, 0);

        //  We first parse the access and modifier flags
        AnnotationNode[] annotations = getAnnotations();
        String accessModifier = getAccess();
        String staticModifier = isStatic() ? "static" : "";
        String finalModifier = isFinal() ? "final" : "";
        String abstractModifier = isAbstract() ? "abstract" : "";
        String classType = getClassType();

        boolean isAnnotation = isAnnotation();

        //  Then we parse the class name
        String className = wrapper.getSimpleName().substring(wrapper.getSimpleName().lastIndexOf('$') + 1);

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

        //  Finally, we insert everything into the pane
        //      Insert annotations
        reader.accept(annotationDecompiler, 0);
        annotationDecompiler.getAnnotationInformations().forEach(info -> {
            //  Create annotation heading
            pane.insert(offset);
            pane.insert("@" + info.name(), pane.getAnnotationStyle());

            if (!info.values().isEmpty()) {
                pane.insert("(", pane.getAnnotationStyle());

                //  Insert annotation values
                int index = 0;
                for (String key : info.values().keySet()) {
                    Pair<Type, Object> value = info.values().get(key);

                    pane.insert(" ");
                    boolean isArray = value.second() instanceof List;
                    boolean isString = value.first().toString().equals("Ljava/lang/String;");
                    boolean isLiteral = value.first().toString().equals("Ljava/lang/Boolean;");

                    pane.insert(key + "=");

                    if (isArray) {
                        List<?> objects = (List<?>) value.second();

                        pane.insert("{", pane.getAnnotationStyle());

                        int listIndex = 0;
                        for (Object object : objects) {
                            if (isString)
                                pane.insert("\"" + object.toString() + "\"", pane.getStringLiteralStyle());
                            else if (isLiteral)
                                pane.insert(object.toString(), pane.getLiteralStyle());
                            else
                                pane.insert(object.toString());

                            if (listIndex == objects.size() - 1)
                                pane.insert("");
                            else
                                pane.insert(", ");

                            listIndex++;
                        }

                        pane.insert("}", pane.getAnnotationStyle());
                    } else {
                        if (isString)
                            pane.insert("\"" + value.second().toString() + "\"", pane.getStringLiteralStyle());
                        else if (isLiteral)
                            pane.insert(value.second().toString(), pane.getLiteralStyle());
                        else
                            pane.insert(value.second().toString());
                    }

                    if (index == info.values().size() - 1)
                        pane.insert(" ");
                    else
                        pane.insert(", ");
                    index++;
                }

                pane.insert(")", pane.getAnnotationStyle());
            }

            pane.insert("\n");
        });

        //      Insert the offset
        pane.insert(offset);

        //      Insert access modifier
        pane.insert(accessModifier, pane.getKeywordStyle());
        pane.insert(accessModifier.isBlank() ? "" : " ");

        //      Insert static modifier
        pane.insert(staticModifier, pane.getKeywordStyle());
        pane.insert(staticModifier.isBlank() ? "" : " ");

        //      Insert final modifier
        pane.insert(finalModifier, pane.getKeywordStyle());
        pane.insert(finalModifier.isBlank() ? "" : " ");

        //      Insert abstract modifier
        pane.insert(abstractModifier, pane.getKeywordStyle());
        pane.insert(abstractModifier.isBlank() ? "" : " ");

        //      Insert class type
        pane.insert(classType, isAnnotation ? pane.getAnnotationStyle() : pane.getKeywordStyle());
        pane.insert(" ");

        //      Insert class name
        pane.insert(className, pane.getTypeStyle());
        pane.insert(" ");

        //      Insert super class
        if (superClassName != null) {
            pane.insert("extends", pane.getKeywordStyle());
            pane.insert(" ");
            pane.insert(superClassName, pane.getTypeStyle());
            pane.insert(" ");
        }

        //      Insert interfaces
        if (!interfaces.isEmpty()) {
            pane.insert("implements", pane.getKeywordStyle());
            pane.insert(" ");

            for (int i = 0; i < interfaces.size(); i++) {
                pane.insert(interfaces.get(i), pane.getTypeStyle());
                pane.insert(i == interfaces.size() - 1 ? " " : ", ");
            }
        }

        //      Insert class opening brace
        pane.insert("{\n\n", null);

        //      Decompile in order:
        //      1. Fields

        //      2. Methods
        //      3. Inner classes
        wrapper.getInnerClasses().forEach(ic -> {
            ClassFileDecompiler decompiler = new ClassFileDecompiler(source, ic, pane, offset + "    ");

            pane.insert("\n");
        });

        //      5. End brace
        pane.insert(offset + "}\n");
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.access = access;
    }

    public AnnotationNode[] getAnnotations() {
        List<AnnotationNode> annotations = new ArrayList<>();

        if (wrapper.getNode().visibleAnnotations != null)
            annotations.addAll(wrapper.getNode().visibleAnnotations);
        if (wrapper.getNode().invisibleAnnotations != null)
            annotations.addAll(wrapper.getNode().invisibleAnnotations);
        if (wrapper.getNode().visibleTypeAnnotations != null)
            annotations.addAll(wrapper.getNode().visibleTypeAnnotations);

        return annotations.toArray(AnnotationNode[]::new);
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

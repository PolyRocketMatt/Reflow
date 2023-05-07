package com.github.polyrocketmatt.reflow.asm.decompilation.custom;

import com.github.polyrocketmatt.reflow.asm.decompilation.asm.AsmAnnotationDecompiler;
import com.github.polyrocketmatt.reflow.asm.decompilation.asm.AsmLocalVarDecompiler;
import com.github.polyrocketmatt.reflow.asm.wrapper.ClassWrapper;
import com.github.polyrocketmatt.reflow.gui.component.FlowStylePane;
import com.github.polyrocketmatt.reflow.utils.AccessUtils;
import com.github.polyrocketmatt.reflow.utils.types.Pair;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.LocalVariableNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.github.polyrocketmatt.reflow.gui.FlowConstants.ASM_VERSION;

public class ClassFileDecompiler extends ClassVisitor {

    private final String source;
    private final ClassWrapper wrapper;
    private final FlowStylePane pane;
    private final String offset;
    private final AsmAnnotationDecompiler annotationDecompiler = new AsmAnnotationDecompiler();
    private final AsmLocalVarDecompiler localVarDecompiler = new AsmLocalVarDecompiler();
    private final List<ConstructorInformation> constructors = new ArrayList<>();
    private final List<MethodInformation> methods = new ArrayList<>();

    private int access = -1;

    public ClassFileDecompiler(String source, ClassWrapper wrapper, FlowStylePane pane, String offset) {
        super(ASM_VERSION);

        this.source = source;
        this.wrapper = wrapper;
        this.pane = pane;
        this.offset = offset;

        decompile();
    }

    public void decompile() {
        String innerOffset = offset + "    ";
        ClassReader reader = new ClassReader(wrapper.getData());

        reader.accept(this, 0);

        //  We first parse the access and modifier flags
        String accessModifier = AccessUtils.getAccess(access);
        String staticModifier = AccessUtils.isStatic(access) ? "static" : "";
        String finalModifier = AccessUtils.isFinal(access) ? "final" : "";
        String abstractModifier = AccessUtils.isAbstract(access) ? "abstract" : "";
        String classType = AccessUtils.getClassType(access);
        boolean isAnnotation =AccessUtils. isAnnotation(access);

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
        pane.insert(separator(accessModifier));

        //      Insert static modifier
        pane.insert(staticModifier, pane.getKeywordStyle());
        pane.insert(separator(staticModifier));

        //      Insert final modifier
        pane.insert(finalModifier, pane.getKeywordStyle());
        pane.insert(separator(finalModifier));

        //      Insert abstract modifier
        pane.insert(abstractModifier, pane.getKeywordStyle());
        pane.insert(separator(abstractModifier));

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
        //      1. Constructors
        for (ConstructorInformation constructor : constructors) {
            List<Pair<String, String>> parameters = constructor.parameters();
            List<String> exceptions = constructor.exceptions();

            pane.insert(innerOffset);
            pane.insert(constructor.accessModifier(), pane.getKeywordStyle());
            pane.insert(" ");
            pane.insert(constructor.constructorName(), pane.getTypeStyle());

            insertMethodSignature(parameters, exceptions);

            pane.insert("{\n");
            pane.insert(innerOffset + "}\n\n");
        }

        //      2. Fields

        //      3. Methods
        for (MethodInformation method : methods) {
            List<String> modifiers = method.modifiers();
            List<Pair<String, String>> parameters = method.parameters();
            List<String> exceptions = method.exceptions();

            pane.insert(innerOffset);
            pane.insert(method.accessModifier(), pane.getKeywordStyle());
            pane.insert(" ");
            modifiers.forEach(modifier -> {
                pane.insert(modifier, pane.getKeywordStyle());
                pane.insert(" ");
            });
            pane.insert(method.returnType(), pane.getTypeStyle());
            pane.insert(" ");
            pane.insert(method.methodName(), pane.getTypeStyle());

            insertMethodSignature(parameters, exceptions);

            pane.insert("{\n");
            pane.insert(innerOffset + "}\n\n");
        }

        //      4. Inner classes
        wrapper.getInnerClasses().stream().sorted(Comparator.comparing(ClassWrapper::getSimpleName)).forEach(ic -> {
            //  Decompiles the inner class
            new ClassFileDecompiler(source, ic, pane, innerOffset);
            pane.insert("\n");
        });

        //      5. End brace
        pane.insert(offset + "}\n");
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.access = access;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        String accessModifier = AccessUtils.getAccess(access);

        if (name.contains("<init>")) {
            String constructorName = wrapper.getSimpleName()
                    .substring(wrapper.getSimpleName().lastIndexOf('.') + 1)
                    .substring(wrapper.getSimpleName().lastIndexOf('$') + 1);
            final List<Pair<String, String>> parameters = new ArrayList<>();
            final Type[] argumentTypes = Type.getArgumentTypes(descriptor);
            MethodVisitor visitor = new MethodVisitor(ASM_VERSION, localVarDecompiler) {
                @Override
                public void visitEnd() {
                    List<LocalVariableNode> localVariables = localVarDecompiler.getLocalVariables();
                    if (localVariables != null)
                        for (int i = 0; i < argumentTypes.length; i++) {
                            String parameterType = argumentTypes[i].getClassName().substring(argumentTypes[i].getClassName().lastIndexOf('.') + 1);
                            String parameterName = localVariables.get(i + 1).name;

                            parameters.add(new Pair<>(parameterType, parameterName));
                        }
                    super.visitEnd();
                }
            };

            //  Add the constructor to the list of constructors
            constructors.add(new ConstructorInformation(accessModifier, constructorName,
                    parameters, (exceptions == null) ? List.of() : List.of(exceptions)));

            return visitor;
        } else {
            List<String> modifiers = new ArrayList<>();

            if (AccessUtils.isStatic(access))           modifiers.add("static");
            if (AccessUtils.isFinal(access))            modifiers.add("final");
            if (AccessUtils.isAbstract(access))         modifiers.add("abstract");
            if (AccessUtils.isSynchronized(access))     modifiers.add("synchronized");
            if (AccessUtils.isNative(access))           modifiers.add("native");
            if (AccessUtils.isStrict(access))           modifiers.add("strictfp");

            String returnType = Type.getReturnType(descriptor).getClassName().substring(Type.getReturnType(descriptor).getClassName().lastIndexOf('.') + 1);
            final List<Pair<String, String>> parameters = new ArrayList<>();
            final Type[] argumentTypes = Type.getArgumentTypes(descriptor);
            MethodVisitor visitor = new MethodVisitor(ASM_VERSION, localVarDecompiler) {
                @Override
                public void visitEnd() {
                    List<LocalVariableNode> localVariables = localVarDecompiler.getLocalVariables();
                    if (localVariables != null)
                        for (int i = 0; i < argumentTypes.length; i++) {
                            String parameterType = argumentTypes[i].getClassName().substring(argumentTypes[i].getClassName().lastIndexOf('.') + 1);
                            String parameterName = localVariables.get(i + 1).name;

                            parameters.add(new Pair<>(parameterType, parameterName));
                        }
                    super.visitEnd();
                }
            };

            //  Add the method to the list of methods
            methods.add(new MethodInformation(accessModifier, modifiers, returnType, name,
                    parameters, (exceptions == null) ? List.of() : List.of(exceptions)));

            return visitor;
        }
    }

    private String separator(String input) {
        return input.isBlank() ? "" : " ";
    }

    private void insertMethodSignature(List<Pair<String, String>> parameters, List<String> exceptions) {
        pane.insert("(");

        for (Pair<String, String> constructorParameter : parameters) {
            pane.insert(constructorParameter.first(), pane.getTypeStyle());
            pane.insert(" ");
            pane.insert(constructorParameter.second());

            if (parameters.indexOf(constructorParameter) != parameters.size() - 1)
                pane.insert(", ");
        }

        pane.insert(")");

        if (!exceptions.isEmpty()) {
            pane.insert(" throws ", pane.getKeywordStyle());
            for (String exception : exceptions) {
                pane.insert(exception, pane.getTypeStyle());
                pane.insert(" ");
            }
        } else
            pane.insert(" ");
    }

    private record ConstructorInformation(String accessModifier, String constructorName,
                                          List<Pair<String, String>> parameters, List<String> exceptions) {
    }

    private record MethodInformation(String accessModifier, List<String> modifiers, String returnType,
                                     String methodName, List<Pair<String, String>> parameters, List<String> exceptions) {
    }

}

package com.github.polyrocketmatt.reflow.asm.decompilation.asm;

import com.github.polyrocketmatt.reflow.utils.types.Pair;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.ASM9;

public class AsmAnnotationDecompiler extends ClassVisitor {

    private List<AnnotationInformation> annotationInformations;

    public AsmAnnotationDecompiler() {
        super(ASM9);
        this.annotationInformations = new ArrayList<>();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        String annotationDescriptor = descriptor.substring(descriptor.lastIndexOf("/") + 1, descriptor.length() - 1);

        return new AnnotationDecompiler(annotationDescriptor, this);
    }

    public List<AnnotationInformation> getAnnotationInformations() {
        return annotationInformations;
    }

    private static class AnnotationDecompiler extends AnnotationVisitor {

        private final String annotationDescriptor;
        private final AsmAnnotationDecompiler annotationDecompiler;
        private final AnnotationInformation information;

        public AnnotationDecompiler(String annotationDescriptor, AsmAnnotationDecompiler asmDecompiler) {
            super(ASM9);
            this.annotationDescriptor = annotationDescriptor;
            this.annotationDecompiler = asmDecompiler;
            this.information = new AnnotationInformation(annotationDescriptor, new HashMap<>());

            //  Create an annotation information
            annotationDecompiler.annotationInformations.add(information);
        }

        @Override
        public void visit(String name, Object value) {
            //  Get the type of the value
            Type type = Type.getType(value.getClass());

            //  Add the value to the annotation information
            information.values().put(name, new Pair<>(type, value));
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public AnnotationVisitor visitArray(String arrayName) {
            return new AnnotationVisitor(ASM9) {
                @Override
                public void visit(String descriptor, Object value) {
                    //  Get the type of the value
                    Type type = Type.getType(value.getClass());

                    //  Add the value to the annotation information
                    if (information.values.containsKey(arrayName))
                        ((ArrayList) information.values().get(arrayName).second()).add(value);
                    else
                        information.values().put(arrayName, new Pair<>(type, new ArrayList<>() {{ add(value); }}));
                }

                @Override
                public void visitEnum(String name, String descriptor, String value) {
                    String descriptorType = descriptor.substring(descriptor.lastIndexOf("/") + 1, descriptor.lastIndexOf(";"));
                    String type = descriptorType + "." + value;

                    //  Add the value to the annotation information
                    if (information.values.containsKey(arrayName))
                        ((ArrayList) information.values().get(arrayName).second()).add(value);
                    else
                        information.values().put(arrayName, new Pair<>(Type.getType(descriptor), new ArrayList<>() {{ add(type); }}));
                }

                @Override
                public AnnotationVisitor visitAnnotation(String name, String descriptor) {
                    return super.visitAnnotation(name, descriptor);
                }

                @Override
                public AnnotationVisitor visitArray(String name) {
                    return super.visitArray(name);
                }
            };
        }

        @Override
        public void visitEnum(String name, String descriptor, String value) {
            String descriptorType = descriptor.substring(descriptor.lastIndexOf("/") + 1, descriptor.lastIndexOf(";"));
            String type = descriptorType + "." + value;

            information.values().put(name, new Pair<>(Type.getType(descriptor), type));
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String descriptor) {
            return new AnnotationDecompiler(descriptor, annotationDecompiler);
        }

        @Override
        public void visitEnd() {}
    }

    public record AnnotationInformation(String name, Map<String, Pair<Type, Object>> values) {}

}

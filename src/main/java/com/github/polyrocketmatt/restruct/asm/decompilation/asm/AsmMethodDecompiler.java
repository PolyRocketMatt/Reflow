package com.github.polyrocketmatt.restruct.asm.decompilation.asm;

import com.github.polyrocketmatt.restruct.asm.decompilation.asm.operand.DoubleOperand;
import com.github.polyrocketmatt.restruct.asm.decompilation.asm.operand.FloatOperand;
import com.github.polyrocketmatt.restruct.asm.decompilation.asm.operand.IntOperand;
import com.github.polyrocketmatt.restruct.asm.decompilation.asm.operand.LongOperand;
import com.github.polyrocketmatt.restruct.asm.decompilation.asm.operand.NullOperand;
import com.github.polyrocketmatt.restruct.asm.decompilation.asm.operand.Operand;
import com.github.polyrocketmatt.restruct.asm.decompilation.asm.operand.ReferenceOperand;
import com.github.polyrocketmatt.restruct.utils.OpcodeUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import static com.github.polyrocketmatt.restruct.gui.FlowConstants.ASM_VERSION;
import static org.objectweb.asm.Opcodes.*;

public class AsmMethodDecompiler extends MethodVisitor {

    private final Stack<Operand<?>> operandStack;
    private final Stack<String> instructionStack;
    private final Map<Integer, String> variableIndexMap;
    private final List<LocalVariableNode> localVariables;
    private final List<String> bytecodeLines;

    private int instructionIndex;
    private String line;

    public AsmMethodDecompiler() {
        super(ASM_VERSION);

        this.operandStack = new Stack<>();
        this.instructionStack = new Stack<>();
        this.variableIndexMap = new HashMap<>();
        this.localVariables = new ArrayList<>();
        this.bytecodeLines = new ArrayList<>();

        this.instructionIndex = 0;
        this.line = "";
    }

    public Stack<String> getInstructionStack() {
        return instructionStack;
    }

    public List<LocalVariableNode> getLocalVariables() {
        return localVariables;
    }

    public List<String> getBytecodeLines() {
        return bytecodeLines;
    }

    @Override
    public void visitParameter(String name, int access) {
        System.out.println("    Visit parameter: " + name + " " + access);
        super.visitParameter(name, access);
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        System.out.println("    Visit annotation default");

        return super.visitAnnotationDefault();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        System.out.println("    Visit annotation: " + descriptor + " " + visible);

        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        System.out.println("    Visit type annotation: " + typeRef + " " + typePath + " " + descriptor + " " + visible);

        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    @Override
    public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
        System.out.println("    Visit annotable parameter count: " + parameterCount + " " + visible);

        super.visitAnnotableParameterCount(parameterCount, visible);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        System.out.println("    Visit parameter annotation: " + parameter + " " + descriptor + " " + visible);

        return super.visitParameterAnnotation(parameter, descriptor, visible);
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        System.out.println("    Visit attribute: " + attribute.type);

        super.visitAttribute(attribute);
    }

    @Override
    public void visitCode() { }

    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        System.out.println("    Visit frame: " + type + " " + numLocal + " " + Arrays.toString(local) + " " + numStack + " " + Arrays.toString(stack));

        super.visitFrame(type, numLocal, local, numStack, stack);
    }

    @Override
    public void visitInsn(int opcode) {
        System.out.println("    Visit: " + OpcodeUtils.translateOpcode(opcode));

        this.addInstruction(opcode);
        switch (opcode) {
            case NOP ->             {}
            //  Push null reference to stack
            case ACONST_NULL ->     operandStack.push(new NullOperand());

            //  Push int constant to stack
            case ICONST_M1 ->       operandStack.push(new IntOperand(-1));
            case ICONST_0 ->        operandStack.push(new IntOperand(0));
            case ICONST_1 ->        operandStack.push(new IntOperand(1));
            case ICONST_2 ->        operandStack.push(new IntOperand(2));
            case ICONST_3 ->        operandStack.push(new IntOperand(3));
            case ICONST_4 ->        operandStack.push(new IntOperand(4));
            case ICONST_5 ->        operandStack.push(new IntOperand(5));

            //  Push long constant to stack
            case LCONST_0 ->        operandStack.push(new LongOperand(0L));
            case LCONST_1 ->        operandStack.push(new LongOperand(1L));

            //  Push float constant to stack
            case FCONST_0 ->        operandStack.push(new FloatOperand(0.0f));
            case FCONST_1 ->        operandStack.push(new FloatOperand(1.0f));
            case FCONST_2 ->        operandStack.push(new FloatOperand(2.0f));

            //  Push double constant to stack
            case DCONST_0 ->        operandStack.push(new DoubleOperand(0.0));
            case DCONST_1 ->        operandStack.push(new DoubleOperand(1.0));

            //  Load a value from array
            case IALOAD ->          {}
            case LALOAD ->          {}
            case FALOAD ->          {}
            case DALOAD ->          {}
            case AALOAD ->          {}
            case BALOAD ->          {}
            case CALOAD ->          {}
            case SALOAD ->          {}
        }

        super.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        System.out.println("    Visit: " + OpcodeUtils.translateOpcode(opcode) + " " + operand);

        this.addInstruction(opcode, operand);
        switch (opcode) {
            case BIPUSH, SIPUSH ->          operandStack.push(new IntOperand(operand));
        }

        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        System.out.println("    Visit: " + OpcodeUtils.translateOpcode(opcode) + " " + varIndex);

        this.addInstruction(opcode, varIndex);
        switch (opcode) {
            case ILOAD ->           operandStack.push(new IntOperand(varIndex));
            case LLOAD ->           operandStack.push(new LongOperand(varIndex));
            case FLOAD ->           operandStack.push(new FloatOperand(varIndex));
            case DLOAD ->           operandStack.push(new DoubleOperand(varIndex));
            case ALOAD ->           operandStack.push(new ReferenceOperand(variableIndexMap.get(varIndex)));
        }
        super.visitVarInsn(opcode, varIndex);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        System.out.println("    Visit: " + OpcodeUtils.translateOpcode(opcode) + " " + type);

        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        System.out.println("    Visit: " + OpcodeUtils.translateOpcode(opcode) + " " + owner + " " + name + " " + descriptor);

        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor) {
        System.out.println("    Visit: " + OpcodeUtils.translateOpcode(opcode) + " " + owner + " " + name + " " + descriptor);

        super.visitMethodInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        System.out.println("    Visit: " + OpcodeUtils.translateOpcode(opcode) + " " + owner + " " + name + " " + descriptor + " " + isInterface);

        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        System.out.println("    Visit: " + name + " " + descriptor + " " + bootstrapMethodHandle + " " + Arrays.toString(bootstrapMethodArguments));

        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        System.out.println("    Visit: " + OpcodeUtils.translateOpcode(opcode) + " " + label);

        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(Label label) {
        System.out.println("    Visit: " + label);

        super.visitLabel(label);
    }

    @Override
    public void visitLdcInsn(Object value) {
        System.out.println("    Visit: " + value);

        this.addInstruction(Opcodes.LDC, value);
        super.visitLdcInsn(value);
    }

    @Override
    public void visitIincInsn(int varIndex, int increment) {
        System.out.println("    Visit: " + varIndex + " " + increment);

        super.visitIincInsn(varIndex, increment);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        System.out.println("    Visit: " + min + " " + max + " " + dflt + " " + Arrays.toString(labels));

        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        System.out.println("    Visit: " + dflt + " " + Arrays.toString(keys) + " " + Arrays.toString(labels));

        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        System.out.println("    Visit: " + descriptor + " " + numDimensions);

        super.visitMultiANewArrayInsn(descriptor, numDimensions);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        System.out.println("    Visit: " + typeRef + " " + typePath + " " + descriptor + " " + visible);

        return super.visitInsnAnnotation(typeRef, typePath, descriptor, visible);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        System.out.println("    Visit: " + start + " " + end + " " + handler + " " + type);

        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        System.out.println("    Visit: " + typeRef + " " + typePath + " " + descriptor + " " + visible);

        return super.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible);
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
        System.out.println("    Visit: " + typeRef + " " + typePath + " " + Arrays.toString(start) + " " + Arrays.toString(end) + " " + Arrays.toString(index) + " " + descriptor + " " + visible);

        return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        System.out.println("    Visit: " + line + " " + start);

        super.visitLineNumber(line, start);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        System.out.println("    Visit: " + maxStack + " " + maxLocals);

        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitEnd() {
        this.bytecodeLines.add("");
        this.instructionIndex = 0;
        System.out.println("\n");
        super.visitEnd();
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        LabelNode startNode = new LabelNode(start);
        LabelNode endNode = new LabelNode(end);
        LocalVariableNode localVariableNode = new LocalVariableNode(name, descriptor, signature, startNode, endNode, index);

        //  Add variable to local variables list and index map
        if (localVariables.stream().noneMatch(var -> var.name.equals(name)))
            localVariables.add(localVariableNode);
        variableIndexMap.putIfAbsent(index, name);

        super.visitLocalVariable(name, descriptor, signature, start, end, index);
    }

    private void addInstruction(String instruction) {
        this.bytecodeLines.add(instructionIndex + ": " + instruction);
        this.instructionIndex++;
    }

    private void addInstruction(int opcode, Object... operands) {
        String instruction = OpcodeUtils.translateOpcode(opcode);
        String operandString = Arrays.stream(operands).map(Object::toString).collect(Collectors.joining(" "));

        this.bytecodeLines.add(instructionIndex + ": " + instruction + "    " + operandString);
        this.instructionIndex++;
    }

}

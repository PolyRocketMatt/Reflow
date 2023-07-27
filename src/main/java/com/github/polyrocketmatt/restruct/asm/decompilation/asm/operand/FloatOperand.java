package com.github.polyrocketmatt.restruct.asm.decompilation.asm.operand;

public record FloatOperand(float operand) implements Operand<Float> {

    @Override
    public Float getOperand() {
        return operand;
    }

}

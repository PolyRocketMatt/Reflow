package com.github.polyrocketmatt.restruct.asm.decompilation.asm.operand;

public record DoubleOperand(double operand) implements Operand<Double> {

    @Override
    public Double getOperand() {
        return operand;
    }

}

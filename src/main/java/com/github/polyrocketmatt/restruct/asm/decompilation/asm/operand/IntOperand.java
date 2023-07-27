package com.github.polyrocketmatt.restruct.asm.decompilation.asm.operand;

public record IntOperand(int operand) implements Operand<Integer> {

    @Override
    public Integer getOperand() {
        return operand;
    }
}

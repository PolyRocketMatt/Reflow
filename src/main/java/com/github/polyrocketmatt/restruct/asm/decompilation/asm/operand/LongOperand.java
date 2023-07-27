package com.github.polyrocketmatt.restruct.asm.decompilation.asm.operand;

public record LongOperand(long operand) implements Operand<Long> {

    @Override
    public Long getOperand() {
        return operand;
    }

}

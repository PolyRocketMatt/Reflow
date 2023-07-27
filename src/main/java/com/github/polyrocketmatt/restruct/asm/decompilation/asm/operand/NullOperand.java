package com.github.polyrocketmatt.restruct.asm.decompilation.asm.operand;

public record NullOperand() implements Operand<Object> {

    @Override
    public Object getOperand() {
        return null;
    }
}

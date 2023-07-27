package com.github.polyrocketmatt.restruct.asm.decompilation.asm.operand;

public record ReferenceOperand(String ref) implements Operand<String> {

    @Override
    public String getOperand() {
        return ref;
    }

}

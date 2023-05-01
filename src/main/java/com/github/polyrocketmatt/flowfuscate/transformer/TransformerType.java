package com.github.polyrocketmatt.flowfuscate.transformer;

public enum TransformerType {

    INPUT_TRANSFORMER("Input"),
    FLOW_OBFUSCATION("Flow Obfuscation"),
    STRING_ENCRYPTION("String Encryption");

    private final String name;

    TransformerType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

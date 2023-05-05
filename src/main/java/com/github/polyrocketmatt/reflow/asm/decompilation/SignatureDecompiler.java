package com.github.polyrocketmatt.reflow.asm.decompilation;

import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

public class SignatureDecompiler extends SignatureVisitor {

    public SignatureDecompiler(int api) {
        super(api);
    }

    public void addSignature(String sign) {
        if (sign != null) {
            new SignatureReader(sign).accept(this);
        }
    }

    public void addTypeSignature(String sign) {
        if (sign!=null) {
            new SignatureReader(sign).acceptType(this);
        }
    }

}

package com.github.polyrocketmatt.reflow.decompiler.wrapper;

import com.github.polyrocketmatt.reflow.decompiler.entity.EntityType;
import org.objectweb.asm.Opcodes;

@FunctionalInterface
public interface EntityWrapper extends Opcodes {

    EntityType getEntityType();

}

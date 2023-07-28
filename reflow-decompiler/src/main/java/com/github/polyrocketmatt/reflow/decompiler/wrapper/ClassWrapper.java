package com.github.polyrocketmatt.reflow.decompiler.wrapper;

import com.github.polyrocketmatt.reflow.decompiler.entity.EntityType;
import com.github.polyrocketmatt.reflow.decompiler.utils.StringUtils;
import org.objectweb.asm.tree.ClassNode;

public class ClassWrapper implements EntityWrapper {

    private final ClassNode asmClassNode;
    private final String simpleName;

    public ClassWrapper(ClassNode asmClassNode) {
        this.asmClassNode = asmClassNode;
        this.simpleName = StringUtils.getLast(asmClassNode.name, "/");
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.CLASS;
    }

    public String getName() {
        return this.asmClassNode.name;
    }

    public String getSimpleName() {
        return this.simpleName;
    }

}

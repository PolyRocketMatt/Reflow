module com.gitub.polyrocketmatt.reflow.decompiler {
    requires org.objectweb.asm;
    requires org.objectweb.asm.commons;
    requires org.objectweb.asm.tree;
    requires org.objectweb.asm.util;
    requires org.objectweb.asm.tree.analysis;

    exports com.github.polyrocketmatt.reflow.decompiler.decompilers;
    exports com.github.polyrocketmatt.reflow.decompiler.entity;
    exports com.github.polyrocketmatt.reflow.decompiler.utils;
    exports com.github.polyrocketmatt.reflow.decompiler.wrapper;
    exports com.github.polyrocketmatt.reflow.decompiler;
}
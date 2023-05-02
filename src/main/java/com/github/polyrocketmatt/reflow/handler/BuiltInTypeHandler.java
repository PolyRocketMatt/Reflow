package com.github.polyrocketmatt.reflow.handler;

import com.github.polyrocketmatt.reflow.utils.types.JavaLangTypes;
import com.github.polyrocketmatt.reflow.wrapper.ClassWrapper;

import java.util.HashSet;
import java.util.Set;

public class BuiltInTypeHandler {

    public static Set<ClassWrapper> getAllBuiltInTypes() {
        Set<ClassWrapper> wrappers = new HashSet<>();

        wrappers.addAll(JavaLangTypes.asWrappers());

        return wrappers;
    }

}

package com.github.polyrocketmatt.restruct.parser;

import org.junit.Test;

import static com.github.polyrocketmatt.restruct.parser.MethodParser.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MethodParserTest {

    @Test
    public void testMethodMatcher() {
        String testSuccess = "  public static final <T extends Comparable<T>> Integer<T>[  ] _identifIer_0(T _Ident, String test    )     {";
        String testFailA = "public public static final <T extends Comparable<T>> Integer<T>[  ] _identifIer_0(T _Ident, String test    )     {";
        String testFailB = "public static final <T extends Comparable<T>> Integer<T>[  ] _identifIer_0(T _Ident, String test    )  ";
        String testFailC = "public static final <T extends Comparable<T>> Integer<T>[  ] _identifIer_0(T _Ident, String test     {";
        String testFailD = "  public static final <T extends Comparable<T>> Integer<T>[  ] _identifIer_0T _Ident, String test    )     {";

        assertTrue(METHOD_PATTERN.matcher(testSuccess).matches());
        assertFalse(METHOD_PATTERN.matcher(testFailA).matches());
        assertFalse(METHOD_PATTERN.matcher(testFailB).matches());
        assertFalse(METHOD_PATTERN.matcher(testFailC).matches());
        assertFalse(METHOD_PATTERN.matcher(testFailD).matches());
    }

}

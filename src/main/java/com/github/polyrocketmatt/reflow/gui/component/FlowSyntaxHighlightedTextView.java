package com.github.polyrocketmatt.reflow.gui.component;

import com.github.polyrocketmatt.reflow.asm.decompilation.custom.ClassFileDecompiler;
import com.github.polyrocketmatt.reflow.asm.wrapper.ClassWrapper;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.polyrocketmatt.reflow.ReFlow.PALETTE;

public class FlowSyntaxHighlightedTextView implements FlowComponent {

    private final String source;
    private final ClassWrapper wrapper;
    private final Set<String> types;
    private final FlowStylePane stylePane;

    public FlowSyntaxHighlightedTextView(ClassWrapper wrapper, String source) {
        this.source = source;
        this.wrapper = wrapper;

        this.types = parseTypes();
        this.stylePane = new FlowStylePane(types);

        //  Keywords
        StyleConstants.setForeground(stylePane.getKeywordStyle(), PALETTE.getKeywordTint());

        //  Literals
        StyleConstants.setForeground(stylePane.getLiteralStyle(), PALETTE.getLiteralTint());

        //  Types
        StyleConstants.setForeground(stylePane.getInternalTypeStyle(), PALETTE.getTypeTint());
        StyleConstants.setForeground(stylePane.getExternalTypeStyle(), PALETTE.getTypeTint());
        StyleConstants.setUnderline(stylePane.getExternalTypeStyle(), true);

        //  Strings
        StyleConstants.setForeground(stylePane.getStringLiteralStyle(), PALETTE.getStringTint());

        //  Annotations
        StyleConstants.setForeground(stylePane.getAnnotationStyle(), PALETTE.getAnnotationStyle());

        parsePackage();
        parseImports();
        parseClass();

        types.forEach(System.out::println);
    }

    @Override
    public JScrollPane getComponent() {
        return stylePane.getComponent();
    }

    @Override
    public void setVisibile(boolean visibility) {
        stylePane.setVisibile(visibility);
    }

    private void insert(StyledDocument document, int length, String part, Style style) {
        try { document.insertString(length, part, style); }
        catch (BadLocationException exception) { exception.printStackTrace(); }
    }

    private Set<String> parseTypes() {
        return wrapper.getImports().stream().map(dependency -> dependency.substring(dependency.lastIndexOf(".") + 1)).collect(Collectors.toSet());
    }

    private void parsePackage() {
        String pkg = (wrapper.getNode().name).replace("/", ".");

        stylePane.insert("package ", stylePane.getKeywordStyle());
        stylePane.insert(pkg + ";\n", null);
        stylePane.insert("\n", null);
    }

    private void parseImports() {
        Set<String> imports = wrapper.getImports();

        //  Sort the imports
        imports = imports.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new));

        //  Remove the object class
        imports.remove("java.lang.Object");

        //  Insert the imports into the document
        imports.forEach(dependency -> {
            stylePane.insert("import ", stylePane.getKeywordStyle());
            stylePane.insert(dependency + ";\n", null);
        });

        stylePane.insert("\n", null);
    }

    private void parseClass() {
        ClassFileDecompiler decompiler = new ClassFileDecompiler(source, wrapper, stylePane, "");
    }

    private static final Set<String> PUNCTUATION = Set.of(
            "(", ")", "{", "}", "[", "]",
            ";", ",", ".", ":", "?", "@", "="
    );

    private static final Set<String> KEYWORDS = Set.of(
            "abstract", "assert",
            "boolean", "break", "byte",
            "case", "catch", "char", "class", "const", "continue",
            "default", "do", "double",
            "else", "enum", "extends",
            "final", "finally", "float", "for",
            "goto",
            "if", "implements", "import", "instanceof", "int", "interface",
            "long",
            "native", "new",
            "package", "private", "protected", "public",
            "record", "return",
            "short", "static", "strictfp", "super", "switch", "synchronized",
            "this", "throw", "throws", "transient", "try",
            "var", "void", "volatile",
            "while"
    );

    private static final Set<String> LITERALS = Set.of("true", "false", "null");

    private record Pair<A, B>(A a, B b) { }

}

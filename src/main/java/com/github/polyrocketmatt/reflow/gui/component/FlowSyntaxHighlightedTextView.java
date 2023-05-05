package com.github.polyrocketmatt.reflow.gui.component;

import com.github.polyrocketmatt.reflow.asm.wrapper.ClassWrapper;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.polyrocketmatt.reflow.ReFlow.PALETTE;

public class FlowSyntaxHighlightedTextView implements FlowComponent {

    private final String source;
    private final ClassWrapper wrapper;
    private final JTextPane textPane;
    private final StyledDocument document;
    private final Set<String> types;

    private final Style keywordStyle;
    private final Style literalStyle;
    private final Style typeStyle;
    private final Style stringStyle;

    public FlowSyntaxHighlightedTextView(ClassWrapper wrapper, String source) {
        this.source = source;
        this.wrapper = wrapper;
        this.textPane = new JTextPane();
        this.textPane.setEditable(false);
        this.textPane.setFont(new Font("Consolas", Font.PLAIN, 12));
        this.textPane.setMargin(new Insets(5, 5, 5, 5));

        this.types = parseTypes(wrapper);
        this.document = textPane.getStyledDocument();

        //  Keywords
        this.keywordStyle = document.addStyle("keyword", null);
        StyleConstants.setForeground(keywordStyle, PALETTE.getKeywordTint());

        //  Literals
        this.literalStyle = document.addStyle("literal", null);
        StyleConstants.setForeground(literalStyle, PALETTE.getLiteralTint());

        //  Types
        this.typeStyle = document.addStyle("type", null);
        StyleConstants.setForeground(typeStyle, PALETTE.getTypeTint());

        //  Strings
        this.stringStyle = document.addStyle("string", null);
        StyleConstants.setForeground(stringStyle, PALETTE.getStringTint());

        types.forEach(System.out::println);

        parsePackage();
        parseImports();
        parseClass();
    }

    @Override
    public JTextPane getComponent() {
        return textPane;
    }

    @Override
    public void setVisibile(boolean visibility) {
        textPane.setVisible(visibility);
    }

    private void insert(StyledDocument document, int length, String part, Style style) {
        try { document.insertString(length, part, style); }
        catch (BadLocationException exception) { exception.printStackTrace(); }
    }

    private void parseWalkedString(StyledDocument document, String walkedString) {
        //  Otherwise, we treat the walked string.
        String[] primitiveParts = walkedString.split(" ");

        //  Re-organize strings
        List<String> parts = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        boolean isBuildingString = false;
        for (String part : primitiveParts) {
            if (part.startsWith("\"")) {
                stringBuilder.append(part).append(" ");

                isBuildingString = true;
            }

            else if (part.endsWith("\"")) {
                stringBuilder.append(part);
                parts.add(stringBuilder.toString());

                //  Reset the string builder
                stringBuilder = new StringBuilder();
                isBuildingString = false;
            }

            else if (isBuildingString)
                stringBuilder.append(part).append(" ");

            else
                parts.add(part);
        }

        for (String part : parts) {
            //  Check if the part is a keyword
            if (KEYWORDS.contains(part))
                insert(document, document.getLength(), part + " ", keywordStyle);

                //  Check if the part is a literal
            else if (LITERALS.contains(part))
                insert(document, document.getLength(), part, literalStyle);

                //  Check if the part is a type
            else if (types.stream().anyMatch(type -> type.equals(part)))
                insert(document, document.getLength(), part + " ", typeStyle);

                //  Check if the part is a string
            else if (part.startsWith("\"") && part.endsWith("\""))
                insert(document, document.getLength(), part, stringStyle);

                //  Otherwise, it's something unknown.
            else if (part.isEmpty())
                insert(document, document.getLength(), " ", null);

            else
                insert(document, document.getLength(), part, null);
        }
    }

    private Pair<String, Integer> walk(String input, int currentIndex) {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        while (index < input.length()) {
            String character = input.substring(index, index + 1);

            if (PUNCTUATION.contains(character)) {
                return new Pair<>(builder.toString(), index + currentIndex);
            } else {
                builder.append(character);
                index++;
            }
        }

        return new Pair<>(builder.toString(), index + currentIndex);
    }

    private Set<String> parseTypes(ClassWrapper wrapper) {
        return wrapper.getImports().stream().map(dependency -> dependency.substring(dependency.lastIndexOf(".") + 1)).collect(Collectors.toSet());
    }

    private void parsePackage() {
        String pkg = (wrapper.getNode().name).replace("/", ".");

        insert(document, document.getLength(), "package ", keywordStyle);
        insert(document, document.getLength(), pkg + ";\n", null);
        insert(document, document.getLength(), "\n", null);
    }

    private void parseImports() {
        Set<String> imports = wrapper.getImports();

        imports.forEach(dependency -> {
            insert(document, document.getLength(), "import ", keywordStyle);
            insert(document, document.getLength(), dependency + ";", null);
            insert(document, document.getLength(), "\n", null);
        });

        insert(document, document.getLength(), "\n", null);
    }

    private void parseClass() {
        String[] lines = source.split("\n");
        for (String line : lines) {
            if (line.isEmpty()) {
                insert(document, document.getLength(), "\n", null);
                continue;
            }

            Pair<String, Integer> walked = walk(line, 0);
            String walkedString = walked.a();
            int punctuationIndex = walked.b();

            while (punctuationIndex != line.length()) {
                //  First, we check if the walked string is empty or blank
                //  In this case, we insert just the punctuation.
                if (walkedString.isEmpty()) {
                    String punctuation = line.substring(punctuationIndex, punctuationIndex + 1);
                    insert(document, document.getLength(), punctuation, null);

                    //  If the punctuation is ";", we insert a new line.
                    if (punctuation.equals(";"))
                        insert(document, document.getLength(), "\n", null);

                    //  We update our loop condition and continue.
                    String unwalkedString = line.substring(punctuationIndex + 1);

                    walked = walk(unwalkedString, punctuationIndex + 1);
                    walkedString = walked.a();
                    punctuationIndex = walked.b();

                    if (punctuationIndex == line.length()) {
                        //  We still handle walkedStrings that are not empty.
                        parseWalkedString(document, walkedString);
                        insert(document, document.getLength(), "\n", null);
                    }
                    continue;
                }

                //  If the string is just a sequence of spaces, we insert the spaces.
                else if (walkedString.chars().allMatch(Character::isWhitespace)) {
                    insert(document, document.getLength(), walkedString, null);

                    //  We update our loop condition and continue.
                    String unwalkedString = line.substring(punctuationIndex);

                    walked = walk(unwalkedString, punctuationIndex);
                    walkedString = walked.a();
                    punctuationIndex = walked.b();

                    if (punctuationIndex == line.length())
                        insert(document, document.getLength(), "\n", null);
                    continue;
                }

                parseWalkedString(document, walkedString);

                //  Here we handle the un-walked string.
                //  The punctuation index is always the index of the first punctuation character.
                //  We can append the punctuation to the end of the document.
                insert(document, document.getLength(), line.substring(punctuationIndex, punctuationIndex + 1), null);

                //  If the punctuation index + 1 == line.length(), then we have reached the end of the line.
                if (punctuationIndex + 1 == line.length()) {
                    //  Insert new line
                    insert(document, document.getLength(), "\n", null);

                    break;
                }

                //  Otherwise, we get the un-walked string
                String unwalkedString = line.substring(punctuationIndex + 1);

                //  We walk the un-walked string
                walked = walk(unwalkedString, punctuationIndex + 1);
                walkedString = walked.a();
                punctuationIndex = walked.b();
            }
        }
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
            "return",
            "short", "static", "strictfp", "super", "switch", "synchronized",
            "this", "throw", "throws", "transient", "try",
            "var", "void", "volatile",
            "while"
    );

    private static final Set<String> LITERALS = Set.of("true", "false", "null");

    private record Pair<A, B>(A a, B b) { }

}

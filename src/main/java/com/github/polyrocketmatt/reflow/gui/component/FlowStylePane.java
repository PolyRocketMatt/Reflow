package com.github.polyrocketmatt.reflow.gui.component;

import com.github.polyrocketmatt.reflow.asm.wrapper.ClassWrapper;
import com.github.polyrocketmatt.reflow.utils.ByteUtils;
import com.github.polyrocketmatt.reflow.utils.TextLineNumber;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.polyrocketmatt.reflow.ReFlow.CLASS_HANDLER;
import static com.github.polyrocketmatt.reflow.ReFlow.INTERFACE;

public class FlowStylePane extends FlowComponent {

    private final JScrollPane scrollPane;
    private final JTextPane pane;
    private final StyledDocument document;
    private final Set<String> types;
    private final Set<String> internalTypes;
    private final Style keywordStyle;
    private final Style literalStyle;
    private final Style internalTypeStyle;
    private final Style externalTypeStyle;
    private final Style stringLiteralStyle;
    private final Style internalAnnotationStyle;
    private final Style externalAnnotationStyle;

    // Create a custom hand cursor
    private final Cursor cursor;

    public FlowStylePane(Set<String> types, Set<String> internalTypes) {
        Font font = new Font("Consolas", Font.PLAIN, 12);

        this.pane = new JTextPane();
        this.document = pane.getStyledDocument();
        this.types = types;
        this.internalTypes = internalTypes;
        this.keywordStyle = document.addStyle("keyword", null);
        this.literalStyle = document.addStyle("literal", null);
        this.internalTypeStyle = document.addStyle("internalType", null);
        this.externalTypeStyle = document.addStyle("externalType", null);
        this.stringLiteralStyle = document.addStyle("stringLiteral", null);
        this.internalAnnotationStyle = document.addStyle("internalAnnotation", null);
        this.externalAnnotationStyle = document.addStyle("externalAnnotation", null);
        this.cursor = new Cursor(Cursor.HAND_CURSOR);

        this.pane.setEditable(false);
        this.pane.setFont(font);
        this.pane.setMargin(new Insets(5, 5, 5, 5));
        this.scrollPane = new JScrollPane(pane);
        this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Add mouse listeners to the text pane
        this.pane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int offset = pane.viewToModel2D(event.getPoint());

                StyledDocument doc = pane.getStyledDocument();
                Element elem = doc.getCharacterElement(offset);
                AttributeSet attrs = elem.getAttributes();

                if (StyleConstants.isUnderline(attrs)) {
                    try {
                        int startOffset = Utilities.getWordStart(pane, offset);
                        int endOffset = Utilities.getWordEnd(pane, offset);

                        String word = doc.getText(startOffset, endOffset - startOffset);

                        //  Find the internal type that contains the word
                        Set<String> primitiveClassNames = internalTypes.stream()
                                .filter(type -> type.contains(word))
                                .collect(Collectors.toSet());

                        //  Check if the current class might be an inner class
                        Set<String> outerClasses = primitiveClassNames.stream().filter(type -> !type.contains("$")).collect(Collectors.toSet());
                        Set<String> innerClasses = primitiveClassNames.stream().filter(type -> type.contains("$")).collect(Collectors.toSet());

                        //  First we check for a match in the outer classes
                        for (String outerClassName : outerClasses) {
                            if (outerClassName.equals(word)) {
                                ClassWrapper wrapper = CLASS_HANDLER.get(outerClassName);
                                String classPath = wrapper.getClassName();
                                FlowClassExplorer explorer = (FlowClassExplorer) INTERFACE.getFlowComponent(FlowClassExplorer.class);

                                if (explorer != null)
                                    explorer.decompileClass(wrapper, classPath, outerClassName);

                                break;
                            }
                        }

                        for (String innerClassName : innerClasses) {
                            if (innerClassName.substring(innerClassName.lastIndexOf("$") + 1).equals(word)) {
                                String className = innerClassName.substring(0, innerClassName.lastIndexOf("$"));
                                ClassWrapper wrapper = CLASS_HANDLER.get(className);
                                String classPath = wrapper.getClassName();
                                FlowClassExplorer explorer = (FlowClassExplorer) INTERFACE.getFlowComponent(FlowClassExplorer.class);

                                if (explorer != null)
                                    explorer.decompileClass(wrapper, classPath, className);

                                break;
                            }
                        }


                        /*
                        //  We find the class-wrapper of the (possible outer) class
                        boolean isInner = primitiveClassName.contains("$");

                        String className = isInner ? primitiveClassName.substring(0, primitiveClassName.indexOf('$')) : primitiveClassName;
                        ClassWrapper wrapper = CLASS_HANDLER.get(className);
                        String classPath = wrapper.getClassName();
                        FlowClassExplorer explorer = (FlowClassExplorer) INTERFACE.getFlowComponent(FlowClassExplorer.class);

                        if (explorer != null)
                            explorer.decompileClass(wrapper, classPath, className);

                         */
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        this.pane.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                int offset = pane.viewToModel2D(event.getPoint());

                StyledDocument doc = pane.getStyledDocument();
                Element elem = doc.getCharacterElement(offset);
                AttributeSet attrs = elem.getAttributes();

                if (StyleConstants.isUnderline(attrs))
                    pane.setCursor(cursor);
                else
                    pane.setCursor(Cursor.getDefaultCursor());
            }
        });

        //  Line numbers?
        this.scrollPane.setRowHeaderView(new TextLineNumber(this.pane, font));

        INTERFACE.register(this);
    }

    @Override
    public JScrollPane getComponent() {
        return scrollPane;
    }

    @Override
    public void setVisible(boolean visibility) {
        pane.setVisible(visibility);
    }

    public void insert(String part, Style style) {
        try { document.insertString(document.getLength(), part, style); }
        catch (BadLocationException exception) { exception.printStackTrace(); }
    }

    public void insert(String part) {
        try { document.insertString(document.getLength(), part, null); }
        catch (BadLocationException exception) { exception.printStackTrace(); }
    }

    public StyledDocument getDocument() {
        return document;
    }

    public Style getKeywordStyle() {
        return keywordStyle;
    }

    public Style getLiteralStyle() {
        return literalStyle;
    }

    public Style getInternalTypeStyle() {
        return internalTypeStyle;
    }

    public Style getExternalTypeStyle() {
        return externalTypeStyle;
    }

    public Style getStringLiteralStyle() {
        return stringLiteralStyle;
    }

    public Style getInternalAnnotationStyle() {
        return internalAnnotationStyle;
    }

    public Style getExternalAnnotationStyle() {
        return externalAnnotationStyle;
    }

    public Style getAnnotationStyle(String annotation) {
        boolean isInternal = internalTypes.stream().anyMatch(iType -> ByteUtils.bytesMatch(iType.getBytes(), annotation.getBytes()));

        return isInternal ? internalAnnotationStyle : externalAnnotationStyle;
    }

    public Style getTypeStyle(String type) {
        boolean isInternal = internalTypes.stream().anyMatch(iType -> ByteUtils.bytesMatch(iType.getBytes(), type.getBytes()));
        return isInternal ? internalTypeStyle : externalTypeStyle;
    }

}

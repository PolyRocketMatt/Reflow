package com.github.polyrocketmatt.reflow.gui.component;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FlowStylePane implements FlowComponent {

    private final JScrollPane scrollPane;
    private final JTextPane pane;
    private final StyledDocument document;
    private final Set<String> types;
    private Style keywordStyle;
    private Style literalStyle;
    private Style typeStyle;
    private Style stringLiteralStyle;
    private Style annotationStyle;

    public FlowStylePane(Set<String> types) {
        this.pane = new JTextPane();
        this.document = pane.getStyledDocument();
        this.types = types;
        this.keywordStyle = document.addStyle("keyword", null);
        this.literalStyle = document.addStyle("literal", null);
        this.typeStyle = document.addStyle("type", null);
        this.stringLiteralStyle = document.addStyle("stringLiteral", null);
        this.annotationStyle = document.addStyle("annotation", null);

        this.pane.setEditable(false);
        this.pane.setFont(new Font("Consolas", Font.PLAIN, 12));
        this.pane.setMargin(new Insets(5, 5, 5, 5));
        this.scrollPane = new JScrollPane(pane);
        this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }

    @Override
    public JScrollPane getComponent() {
        return scrollPane;
    }

    @Override
    public void setVisibile(boolean visibility) {
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

    public Style getTypeStyle() {
        return typeStyle;
    }

    public Style getStringLiteralStyle() {
        return stringLiteralStyle;
    }

    public Style getAnnotationStyle() {
        return annotationStyle;
    }
}

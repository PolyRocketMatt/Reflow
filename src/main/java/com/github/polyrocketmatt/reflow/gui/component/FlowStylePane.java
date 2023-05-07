package com.github.polyrocketmatt.reflow.gui.component;

import com.github.polyrocketmatt.reflow.utils.ByteUtils;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.Set;

public class FlowStylePane implements FlowComponent {

    private final JScrollPane scrollPane;
    private final JTextPane pane;
    private final StyledDocument document;
    private final Set<String> types;
    private final Set<String> internalTypes;
    private Style keywordStyle;
    private Style literalStyle;
    private Style internalTypeStyle;
    private Style externalTypeStyle;
    private Style stringLiteralStyle;
    private Style internalAnnotationStyle;
    private Style externalAnnotationStyle;

    // Create a custom hand cursor
    private final Cursor cursor;

    public FlowStylePane(Set<String> types, Set<String> internalTypes) {
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
        this.pane.setFont(new Font("Consolas", Font.PLAIN, 12));
        this.pane.setMargin(new Insets(5, 5, 5, 5));
        this.scrollPane = new JScrollPane(pane);
        this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Add a hyperlink listener to the text pane
        this.pane.addHyperlinkListener(event -> {
            if (event.getEventType() == HyperlinkEvent.EventType.ENTERED)
                pane.setCursor(cursor);
            else if (event.getEventType() == HyperlinkEvent.EventType.EXITED)
                pane.setCursor(Cursor.getDefaultCursor());
            else {
                System.out.println(event.getDescription());
            }
        });
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

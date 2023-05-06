package com.github.polyrocketmatt.reflow.gui.palette;

import com.formdev.flatlaf.FlatLightLaf;

import java.awt.*;

public class LightFlowPalette extends FlowPalette {

    //  General Theme
    private static final Color TINT = new Color(Integer.parseInt("3C3F41", 16));
    private static final Color RED = new Color(Integer.parseInt("CC5A71", 16));
    private static final Color SELECT = new Color(Integer.parseInt("E0E0E0", 16));
    private static final Color UNSELECT = new Color(Integer.parseInt("F2F2F2", 16));
    private static final Color MENU_HOVER = new Color(Integer.parseInt("D9D9D9", 16));
    private static final Color MENU_SELECT_BACKGROUND = new Color(Integer.parseInt("D9D9D9", 16));
    private static final Color MENU_SELECT_FOREGROUND = new Color(Integer.parseInt("000000", 16));

    //  Syntax Highlighting
    private static final Color KEYWORD = new Color(Integer.parseInt("3891A6", 16));
    private static final Color LITERAL = new Color(Integer.parseInt("3891A6", 16));
    private static final Color TYPE = new Color(Integer.parseInt("CC5A71", 16));
    private static final Color STRING = new Color(Integer.parseInt("659B5E", 16));
    private static final Color ANNOTATION = new Color(Integer.parseInt("A2A2A2", 16));

    public LightFlowPalette() {
        super();
    }

    @Override
    public void install() {
        FlatLightLaf.setup();
    }

    @Override
    public Color getTint() {
        return TINT;
    }

    @Override
    public Color getRed() {
        return RED;
    }

    @Override
    public Color getSelect() {
        return SELECT;
    }

    @Override
    public Color getUnselect() {
        return UNSELECT;
    }

    @Override
    public Color getMenuHover() {
        return MENU_HOVER;
    }

    @Override
    public Color getMenuSelectBackground() {
        return MENU_SELECT_BACKGROUND;
    }

    @Override
    public Color getMenuSelectForeground() {
        return MENU_SELECT_FOREGROUND;
    }

    @Override
    public Color getKeywordTint() {
        return KEYWORD;
    }

    @Override
    public Color getLiteralTint() {
        return LITERAL;
    }

    @Override
    public Color getTypeTint() {
        return TYPE;
    }

    @Override
    public Color getStringTint() {
        return STRING;
    }

    @Override
    public Color getAnnotationStyle() {
        return ANNOTATION;
    }
}

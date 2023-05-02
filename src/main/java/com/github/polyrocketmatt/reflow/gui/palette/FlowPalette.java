package com.github.polyrocketmatt.reflow.gui.palette;

import java.awt.*;

public abstract class FlowPalette {

    public FlowPalette() {
        install();
    }

    //  Flatlaf Installation
    public abstract void install();

    //  General theme
    public abstract Color getTint();

    public abstract Color getRed();

    //  Menu
    public abstract Color getSelect();

    public abstract Color getUnselect();

    public abstract Color getMenuHover();

    public abstract Color getMenuSelectBackground();

    public abstract Color getMenuSelectForeground();

    //  Syntax Highlighting
    public abstract Color getKeywordTint();

    public abstract Color getLiteralTint();

    public abstract Color getTypeTint();

    public abstract Color getStringTint();

}

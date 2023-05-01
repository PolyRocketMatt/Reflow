package com.github.polyrocketmatt.flowfuscate.gui.item;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.github.polyrocketmatt.flowfuscate.gui.GuiConstants.DIM_22;

public class MenuActionItem {

    private final JMenuItem item;

    public MenuActionItem(String name) {
        this.item = new JMenuItem(name);
        this.item.setPreferredSize(DIM_22);
    }

    public MenuActionItem with(ActionListener listener) {
        item.addActionListener(listener);
        return this;
    }

    public MenuActionItem icon(Icon icon) {
        item.setIcon(icon);
        return this;
    }

    public MenuActionItem size(Dimension dimension) {
        item.setPreferredSize(dimension);
        return this;
    }

    public JMenuItem get() {
        return item;
    }

}

package com.github.polyrocketmatt.reflow.gui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class FlowMenuItem implements FlowComponent {

    private final JMenuItem item;

    public FlowMenuItem(String name) {
        this.item = new JMenuItem(name);
    }

    public FlowMenuItem withListener(ActionListener listener) {
        item.addActionListener(listener);
        return this;
    }

    public FlowMenuItem withIcon(Icon icon) {
        item.setIcon(icon);
        return this;
    }

    public FlowMenuItem withSize(int width, int height) {
        item.setPreferredSize(new Dimension(width, height));
        return this;
    }

    @Override
    public JMenuItem getComponent() {
        return item;
    }

    @Override
    public void setVisibile(boolean visibility) {

    }
}

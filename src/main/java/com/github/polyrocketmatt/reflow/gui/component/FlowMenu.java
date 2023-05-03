package com.github.polyrocketmatt.reflow.gui.component;

import javax.swing.*;

public class FlowMenu implements FlowComponent {

    private final JMenu menu;

    public FlowMenu(String name) {
        this.menu = new JMenu(name);
    }

    public void add(FlowMenuItem item) {
        menu.add(item.getComponent());
    }

    @Override
    public JMenu getComponent() {
        return menu;
    }

    @Override
    public void setVisibile(boolean visibility) {

    }
}

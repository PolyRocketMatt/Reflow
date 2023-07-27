package com.github.polyrocketmatt.restruct.gui.component;

import javax.swing.*;

public class FlowMenu extends FlowComponent {

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
    public void setVisible(boolean visibility) {

    }
}

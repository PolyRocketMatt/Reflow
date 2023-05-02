package com.github.polyrocketmatt.reflow.gui.component;

import javax.swing.*;

public class FlowMenuBar implements FlowComponent {

    private final JMenuBar menuBar;

    public FlowMenuBar() {
        this.menuBar = new JMenuBar();
    }

    public void add(FlowMenu menu) {
        menuBar.add(menu.getComponent());
    }

    @Override
    public JMenuBar getComponent() {
        return menuBar;
    }
}

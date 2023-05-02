package com.github.polyrocketmatt.reflow.gui.component;

import javax.swing.*;
import java.awt.*;

public class FlowTab implements FlowComponent {

    private final String tabName;
    private final JPanel panel;

    public FlowTab(String tabName) {
        this.tabName = tabName;
        this.panel = new JPanel(new BorderLayout());
    }

    @Override
    public JPanel getComponent() {
        return panel;
    }

    public String getTabName() {
        return tabName;
    }
}

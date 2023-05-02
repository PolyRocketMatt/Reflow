package com.github.polyrocketmatt.reflow.gui.component;

import javax.swing.*;
import java.awt.*;

public class FlowPanel implements FlowComponent {

    private final int index;
    private final JPanel panel;

    public FlowPanel(int index) {
        this.index = index;
        this.panel = new JPanel();
    }

    public FlowPanel(int index, LayoutManager manager) {
        this.index = index;
        this.panel = new JPanel(manager);
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public JPanel getComponent() {
        return panel;
    }
}

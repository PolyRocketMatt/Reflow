package com.github.polyrocketmatt.restruct.gui.component;

import javax.swing.*;
import java.awt.*;

import static com.github.polyrocketmatt.restruct.Restruct.INTERFACE;

public class FlowPanel extends FlowComponent {

    private final JPanel panel;

    public FlowPanel(int index) {
        super(index);
        this.panel = new JPanel();

        INTERFACE.register(this);
    }

    public FlowPanel(int index, LayoutManager manager) {
        this.index = index;
        this.panel = new JPanel(manager);
    }

    @Override
    public JPanel getComponent() {
        return panel;
    }

    @Override
    public void setVisible(boolean visibility) {
        panel.setVisible(visibility);
    }
}

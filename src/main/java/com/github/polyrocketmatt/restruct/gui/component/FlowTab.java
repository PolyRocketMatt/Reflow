package com.github.polyrocketmatt.restruct.gui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.github.polyrocketmatt.restruct.Restruct.INTERFACE;
import static com.github.polyrocketmatt.restruct.Restruct.PALETTE;

public class FlowTab extends FlowComponent {

    private final String tabName;
    private final JPanel panel;
    private final JPanel tabComponent;

    public FlowTab(String tabName, JPanel panel, JPanel tabComponent) {
        this.tabName = tabName;
        this.panel = panel;
        this.tabComponent = tabComponent;
        this.tabComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                setBackground(PALETTE.getMenuHover());
            }

            @Override
            public void mouseExited(MouseEvent event) {
                setBackground(PALETTE.getUnselect());
            }

            @Override
            public void mouseClicked(MouseEvent event) {
                setBackground(PALETTE.getMenuHover());
            }
        });

        INTERFACE.register(this);
    }

    @Override
    public JPanel getComponent() {
        return panel;
    }

    @Override
    public void setVisible(boolean visibility) {
        panel.setVisible(visibility);
    }

    public String getTabName() {
        return tabName;
    }

    public Component getTabComponent() {
        return tabComponent;
    }

    public void setBackground(Color color) {
        panel.setBackground(color);
        tabComponent.setBackground(color);
    }

}

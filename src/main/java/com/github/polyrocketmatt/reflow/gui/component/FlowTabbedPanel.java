package com.github.polyrocketmatt.reflow.gui.component;

import com.github.polyrocketmatt.reflow.gui.icon.FlowCloseIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.polyrocketmatt.reflow.ReFlow.PALETTE;
import static com.github.polyrocketmatt.reflow.gui.FlowConstants.DIM_17;

public class FlowTabbedPanel implements FlowComponent {

    private final JTabbedPane pane;
    private final List<FlowTab> tabs;
    private final Map<Integer, JPanel> iconPanels;
    private final boolean isCloseable;

    public FlowTabbedPanel(boolean isCloseable) {
        this.pane = new JTabbedPane();
        this.tabs = new ArrayList<>();
        this.iconPanels = new HashMap<>();
        this.isCloseable = isCloseable;
    }

    public void add(FlowTab tab) {
        tabs.add(tab);
        pane.addTab(tab.getTabName(), tab.getComponent());

        if (isCloseable)
            pane.setTabComponentAt(pane.getTabCount() - 1, getIconPanel(pane, tab.getComponent(), tab.getTabName(), pane.getTabCount() - 1));
    }

    @Override
    public JTabbedPane getComponent() {
        return pane;
    }

    @Override
    public void setVisibile(boolean visibility) {
        tabs.forEach(tab -> tab.setVisibile(visibility));
        pane.setVisible(visibility);
    }

    public List<FlowTab> getTabs() {
        return tabs;
    }

    public FlowTab getTabByIndex(int index) {
        if (index < 0 || index >= tabs.size())
            throw new IllegalArgumentException("Invalid tab index: " + index + " (size: " + tabs.size() + ")");
        return tabs.get(index);
    }

    public FlowTab getTabByName(String name) {
        for (FlowTab tab : tabs)
            if (tab.getTabName().equals(name))
                return tab;
        return null;
    }

    public int getIndexOf(FlowTab tab) {
        return tabs.indexOf(tab);
    }

    public boolean isCloseable() {
        return isCloseable;
    }

    public void setSelectedTab(int index) {
        if (index < 0 || index >= tabs.size())
            throw new IllegalArgumentException("Invalid tab index");
        pane.setSelectedIndex(index);
    }

    public void setSelectedTab(FlowTab tab) {
        pane.setSelectedComponent(tab.getComponent());
        updateColors();
    }

    public FlowTab getSelectedTab() {
        return tabs.get(pane.getSelectedIndex());
    }

    public void updateColors() {
        int selectedIndex = pane.getSelectedIndex();
        for (Map.Entry<Integer, JPanel> iconPanelEntry : iconPanels.entrySet())
            iconPanelEntry.getValue().setBackground(selectedIndex == iconPanelEntry.getKey() ? PALETTE.getMenuSelectBackground() : PALETTE.getUnselect());
    }

    private JPanel getIconPanel(JTabbedPane tabbedPane, JPanel panel, String title, int index) {
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel iconLabel = new JLabel(title);

        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        iconPanel.add(iconLabel);

        JButton closeButton = new JButton();
        closeButton.setIcon(new FlowCloseIcon(DIM_17));
        closeButton.setBackground(PALETTE.getMenuSelectBackground());
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                tabbedPane.remove(panel);
                iconPanels.remove(index);
                updateColors();
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                closeButton.setBackground(PALETTE.getRed());
                iconPanel.setBackground(PALETTE.getMenuSelectBackground());
            }

            @Override
            public void mouseExited(MouseEvent event) {
                closeButton.setBackground(PALETTE.getMenuSelectBackground());
                iconPanel.setBackground(PALETTE.getUnselect());
            }
        });

        iconPanel.setBackground(PALETTE.getMenuSelectBackground());
        iconPanel.add(closeButton);
        iconPanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                tabbedPane.setSelectedComponent(panel);
                iconPanel.setBackground(PALETTE.getMenuSelectBackground());
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                iconPanel.setBackground(PALETTE.getMenuSelectBackground());
            }

            @Override
            public void mouseExited(MouseEvent event) {
                if (!tabbedPane.getSelectedComponent().equals(panel))
                    iconPanel.setBackground(PALETTE.getUnselect());
            }
        });

        iconPanels.put(index, iconPanel);

        return iconPanel;
    }

}

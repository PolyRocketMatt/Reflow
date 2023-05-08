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

import static com.github.polyrocketmatt.reflow.ReFlow.INTERFACE;
import static com.github.polyrocketmatt.reflow.ReFlow.PALETTE;
import static com.github.polyrocketmatt.reflow.gui.FlowConstants.DIM_17;

public class FlowTabbedPanel extends FlowComponent {

    private final JTabbedPane tabPane;
    private final List<FlowTab> tabs;
    private final Map<Integer, JPanel> iconPanels;
    private final boolean isCloseable;

    public FlowTabbedPanel(boolean isCloseable) {
        this.tabPane = new JTabbedPane();
        this.tabs = new ArrayList<>();
        this.iconPanels = new HashMap<>();
        this.isCloseable = isCloseable;

        INTERFACE.register(this);
    }

    public FlowTab add(String name) {
        int tabIndex = tabPane.getTabCount();
        JPanel panel = new JPanel(new BorderLayout());
        FlowTab tab = new FlowTab(name, panel, getIconPanel(panel, name, tabIndex));

        tabs.add(tab);
        tabPane.addTab(tab.getTabName(), tab.getComponent());

        if (isCloseable)
            tabPane.setTabComponentAt(tabPane.getTabCount() - 1, tab.getTabComponent());

        return tab;
    }

    @Override
    public JTabbedPane getComponent() {
        return tabPane;
    }

    @Override
    public void setVisible(boolean visibility) {
        tabs.forEach(tab -> tab.setVisible(visibility));
        tabPane.setVisible(visibility);
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
        tabPane.setSelectedIndex(index);
    }

    public void setSelectedTab(FlowTab tab) {
        tabPane.setSelectedComponent(tab.getComponent());
        updateColors();
    }

    public FlowTab getSelectedTab() {
        return tabs.get(tabPane.getSelectedIndex());
    }

    public void updateColors() {
        int selectedIndex = tabPane.getSelectedIndex();
        for (Map.Entry<Integer, JPanel> iconPanelEntry : iconPanels.entrySet())
            iconPanelEntry.getValue().setBackground(selectedIndex == iconPanelEntry.getKey() ? PALETTE.getMenuSelectBackground() : PALETTE.getUnselect());
    }

    private JPanel getIconPanel(JPanel panel, String title, int index) {
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
                tabPane.remove(panel);
                tabs.remove(index);
                iconPanels.remove(index);
                updateColors();

                System.out.println(tabPane.getTabCount());
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
                tabPane.setSelectedComponent(panel);
                iconPanel.setBackground(PALETTE.getMenuSelectBackground());
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                iconPanel.setBackground(PALETTE.getMenuSelectBackground());
            }

            @Override
            public void mouseExited(MouseEvent event) {
                if (!tabPane.getSelectedComponent().equals(panel))
                    iconPanel.setBackground(PALETTE.getUnselect());
            }
        });

        iconPanels.put(index, iconPanel);

        return iconPanel;
    }

}

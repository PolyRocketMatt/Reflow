package com.github.polyrocketmatt.reflow.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.github.polyrocketmatt.reflow.ReFlow;
import com.github.polyrocketmatt.reflow.gui.icon.FlowCloseIcon;

import javax.swing.*;

import java.awt.*;

import static com.github.polyrocketmatt.reflow.ReFlow.PALETTE;

public class FlowInterfaceManager {

    public FlowInterfaceManager() {
        //  Look and feel
        try {
            UIManager.setLookAndFeel(ReFlow.DARK_THEME ? new FlatDarkLaf() : new FlatLightLaf());

            //  Menu
            UIManager.put("MenuBar.hoverBackGround",            PALETTE.getMenuHover());
            UIManager.put("MenuBar.selectionBackground",        PALETTE.getMenuSelectBackground());
            UIManager.put("MenuBar.selectionForeground",        PALETTE.getMenuSelectForeground());

            //  Menu Item
            UIManager.put("MenuItem.selectionBackground",       PALETTE.getMenuSelectBackground());
            UIManager.put("MenuItem.selectionForeground",       PALETTE.getMenuSelectForeground());

            //  Tabbed Panes
            UIManager.put("TabbedPane.showTabSeparators",       true);
            UIManager.put("TabbedPane.underlineColor",          PALETTE.getTint());
            UIManager.put("TabbedPane.inactiveUnderlineColor",  PALETTE.getTint());
            UIManager.put("TabbedPane.selectedBackground",      PALETTE.getMenuSelectBackground());
            UIManager.put("TabbedPane.selectedForeground",      PALETTE.getMenuSelectForeground());
            UIManager.put("TabbedPane.hoverColor",              PALETTE.getMenuSelectBackground());
            UIManager.put("TabbedPane.hoverForeground",         PALETTE.getMenuSelectForeground());
            UIManager.put("TabbedPane.closeHoverForeground",    PALETTE.getRed());
            UIManager.put("TabbedPane.closePressedForeground",  PALETTE.getRed());
            UIManager.put("TabbedPane.closeIcon",               new FlowCloseIcon(new Dimension(12, 12)));

            //  Tree
            UIManager.put("Tree.selectionBackground",           PALETTE.getMenuSelectBackground());
            UIManager.put("Tree.selectionForeground",           PALETTE.getMenuSelectForeground());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

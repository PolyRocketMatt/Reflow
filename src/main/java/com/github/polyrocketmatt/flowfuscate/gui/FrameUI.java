package com.github.polyrocketmatt.flowfuscate.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.github.polyrocketmatt.flowfuscate.FlowFuscate;

import javax.swing.*;
import java.awt.*;

public class FrameUI {

    protected final JFrame frame;
    private final boolean maximize;

    public FrameUI(String name, boolean maximize) {
        //  Initialise Flatlaf
        if (FlowFuscate.DARK_THEME)
            FlatDarkLaf.setup();
        else
            FlatLightLaf.setup();

        //  Look and feel
        try {
            UIManager.setLookAndFeel(FlowFuscate.DARK_THEME ? new FlatDarkLaf() : new FlatLightLaf());
            UIManager.put("Separator.foreground", FlowPalette.DARK);
            UIManager.put("TabbedPane.showTabSeparators", true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        this.frame = new JFrame(name);
        this.maximize = maximize;
    }

    public void finish() {
        if (maximize)
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}

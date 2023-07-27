package com.github.polyrocketmatt.restruct;

import com.github.polyrocketmatt.restruct.gui.FlowInterface;
import com.github.polyrocketmatt.restruct.gui.palette.FlowPalette;
import com.github.polyrocketmatt.restruct.gui.palette.LightFlowPalette;
import com.github.polyrocketmatt.restruct.handler.ClassHandler;

import java.io.File;
import java.util.UUID;

public class Restruct {

    public static final boolean DARK_THEME = false;
    public static final File TEMP_DIR = new File(System.getenv("APPDATA") + "/reflow/tmp_" + UUID.randomUUID());
    public static FlowPalette PALETTE;
    public static FlowInterface INTERFACE;
    public static ClassHandler CLASS_HANDLER;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) {
        //  Initialize the tmp directory
        TEMP_DIR.mkdirs();

        //  TODO: Add dark theme
        PALETTE = new LightFlowPalette();
        INTERFACE = new FlowInterface();
        INTERFACE.initialise();

        CLASS_HANDLER = ClassHandler.INSTANCE;
    }

}

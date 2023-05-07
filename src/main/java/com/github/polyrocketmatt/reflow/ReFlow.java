package com.github.polyrocketmatt.reflow;

import com.github.polyrocketmatt.reflow.gui.FlowInterface;
import com.github.polyrocketmatt.reflow.gui.palette.FlowPalette;
import com.github.polyrocketmatt.reflow.gui.palette.LightFlowPalette;
import com.github.polyrocketmatt.reflow.handler.ClassHandler;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.util.UUID;

public class ReFlow {

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
        CLASS_HANDLER = ClassHandler.INSTANCE;
    }

}

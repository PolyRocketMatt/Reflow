package com.github.polyrocketmatt.reflow.gui.component;

import java.awt.*;

public interface FlowComponent {

    default int getIndex() {
        return -1;
    }

    Component getComponent();

    void setVisibile(boolean visibility);

}

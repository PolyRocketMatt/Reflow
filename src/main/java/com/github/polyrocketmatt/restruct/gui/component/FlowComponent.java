package com.github.polyrocketmatt.restruct.gui.component;

import java.awt.*;

public abstract class FlowComponent {

    public int index;

    public FlowComponent(int index) {
        this.index = index;
    }

    public FlowComponent() {
        this.index = -1;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public abstract Component getComponent();

    public abstract void setVisible(boolean visibility);

}

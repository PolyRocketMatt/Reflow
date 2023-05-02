package com.github.polyrocketmatt.reflow.gui.icon;

import com.formdev.flatlaf.icons.FlatWindowAbstractIcon;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.util.SystemInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

import static com.github.polyrocketmatt.reflow.ReFlow.PALETTE;

public class FlowCloseIcon extends FlatWindowAbstractIcon implements FlowIcon {

    private final Color hoverForeground = UIManager.getColor("TabbedPane.closeHoverForeground");
    private final Color pressedForeground = UIManager.getColor("TabbedPane.closePressedForeground");

    public FlowCloseIcon(Dimension dimension) {
        super(dimension, UIManager.getColor("TabbedPane.closeHoverForeground"), UIManager.getColor("TabbedPane.closePressedForeground"));
    }

    protected void paintIconAt1x(Graphics2D g, int x, int y, int width, int height, double scaleFactor) {
        g.setColor(PALETTE.getMenuSelectForeground());

        int iwh = (int) (10.0 * scaleFactor);
        int ix = x + (width - iwh) / 2;
        int iy = y + (height - iwh) / 2;
        int ix2 = ix + iwh - 1;
        int iy2 = iy + iwh - 1;
        float thickness = SystemInfo.isWindows_11_orLater ? (float)scaleFactor : (float)((int)scaleFactor);
        Path2D path = new Path2D.Float(0, 4);
        path.moveTo(ix, iy);
        path.lineTo(ix2, iy2);
        path.moveTo(ix, iy2);
        path.lineTo(ix2, iy);
        g.setStroke(new BasicStroke(thickness));
        g.draw(path);
    }

    protected Color getForeground(Component c) {
        return FlatButtonUI.buttonStateColor(c, c.getForeground(), (Color)null, (Color)null, this.hoverForeground, this.pressedForeground);
    }

}

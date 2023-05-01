package com.github.polyrocketmatt.flowfuscate.gui.icon;

import com.formdev.flatlaf.icons.FlatWindowAbstractIcon;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.util.SystemInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class FlowWindowCloseIcon extends FlatWindowAbstractIcon {

    private final Color hoverForeground = UIManager.getColor("TitlePane.closeHoverForeground");
    private final Color pressedForeground = UIManager.getColor("TitlePane.closePressedForeground");

    public FlowWindowCloseIcon(Dimension dimension) {
        super(dimension, UIManager.getColor("TitlePane.closeHoverBackground"), UIManager.getColor("TitlePane.closePressedBackground"));
    }

    protected void paintIconAt1x(Graphics2D g, int x, int y, int width, int height, double scaleFactor) {
        int iwh = (int)(10.0 * scaleFactor);
        int ix = x + (width - iwh) / 2;
        int iy = y + (height - iwh) / 2;
        int ix2 = ix + iwh - 1;
        int iy2 = iy + iwh - 1;
        float thickness = SystemInfo.isWindows_11_orLater ? (float)scaleFactor : (float)((int)scaleFactor);
        Path2D path = new Path2D.Float(0, 4);
        path.moveTo((double)ix, (double)iy);
        path.lineTo((double)ix2, (double)iy2);
        path.moveTo((double)ix, (double)iy2);
        path.lineTo((double)ix2, (double)iy);
        g.setStroke(new BasicStroke(thickness));
        g.draw(path);
    }

    protected Color getForeground(Component c) {
        return FlatButtonUI.buttonStateColor(c, c.getForeground(), (Color)null, (Color)null, this.hoverForeground, this.pressedForeground);
    }
}

package com.github.polyrocketmatt.reflow.palette;

import com.github.polyrocketmatt.reflow.decompiler.utils.StringUtils;

public record ReflowColor(
        int red,
        int green,
        int blue
) {

    public int r() {
        return red;
    }

    public int g() {
        return green;
    }

    public int b() {
        return blue;
    }

    public int rgb() {
        return (red << 16) | (green << 8) | blue;
    }

    public int argb() {
        return (0xFF << 24) | (red << 16) | (green << 8) | blue;
    }

    public String hex() {
        return StringUtils.rgbToHex(red, green, blue);
    }

    public static ReflowColor lerp(ReflowColor first, ReflowColor second, double t) {
        return new ReflowColor(
                (int) (first.red * (1 - t) + second.red * t),
                (int) (first.green * (1 - t) + second.green * t),
                (int) (first.blue * (1 - t) + second.blue * t)
        );
    }

}

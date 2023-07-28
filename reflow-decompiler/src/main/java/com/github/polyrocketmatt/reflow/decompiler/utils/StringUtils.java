package com.github.polyrocketmatt.reflow.decompiler.utils;

public class StringUtils {

    public static String getLast(String string, String delimiter) {
        String[] split = string.split(delimiter);
        return split[split.length - 1];
    }

    public static String rgbToHex(int red, int green, int blue) {
        // Ensure that the RGB values are within the valid range (0-255)
        red = Math.min(255, Math.max(0, red));
        green = Math.min(255, Math.max(0, green));
        blue = Math.min(255, Math.max(0, blue));

        // Convert the RGB values to hexadecimal strings
        String hexRed = Integer.toHexString(red);
        String hexGreen = Integer.toHexString(green);
        String hexBlue = Integer.toHexString(blue);

        // Pad the strings with leading zeros if needed
        hexRed = padLeftZero(hexRed);
        hexGreen = padLeftZero(hexGreen);
        hexBlue = padLeftZero(hexBlue);

        // Combine the RGB values into a single HEX string
        String hexColor = hexRed + hexGreen + hexBlue;

        return hexColor.toUpperCase(); // Convert to uppercase for consistency
    }

    public static String padLeftZero(String hexValue) {
        return hexValue.length() == 1 ? "0" + hexValue : hexValue;
    }

}

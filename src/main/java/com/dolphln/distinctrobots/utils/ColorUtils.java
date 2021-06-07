package com.dolphln.distinctrobots.utils;

import org.bukkit.Color;

public class ColorUtils {

    public static Color hexToColor(String hex) {
        return Color.fromRGB(
                Integer.valueOf(hex.substring(1, 3),16),
                Integer.valueOf(hex.substring(3, 5),16),
                Integer.valueOf(hex.substring(5, 7),16));
    }

}

package com.world.back.utils;

import java.text.DecimalFormat;

public class FileUtil {

    private static final String[] UNITS = {"B", "KB", "MB", "GB", "TB"};

    public static String formatFileSize(long size) {
        if (size <= 0) return "0 B";

        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        if (digitGroups >= UNITS.length) {
            digitGroups = UNITS.length - 1;
        }

        DecimalFormat df = new DecimalFormat("#,##0.#");
        String formattedSize = df.format(size / Math.pow(1024, digitGroups));

        return formattedSize + " " + UNITS[digitGroups];
    }
}
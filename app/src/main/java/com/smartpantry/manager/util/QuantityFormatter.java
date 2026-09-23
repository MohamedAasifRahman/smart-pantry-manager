package com.smartpantry.manager.util;

import java.util.Locale;

// Quantities are stored as REAL so half an onion can be recorded, but showing

// "2.0 pcs" would look wrong. Whole numbers print without a decimal part.
public final class QuantityFormatter {

    private QuantityFormatter() {
    }

    // Returns for example "2", "0.5" or "1.25".
    public static String format(double quantity) {
        if (quantity == Math.rint(quantity) && !Double.isInfinite(quantity)) {
            return String.format(Locale.getDefault(), "%d", (long) quantity);
        }
        String formatted = String.format(Locale.getDefault(), "%.2f", quantity);
        // Trim trailing zeros so 1.50 reads as 1.5.
        while (formatted.endsWith("0")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }
        if (formatted.endsWith(".") || formatted.endsWith(",")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }
        return formatted;
    }

    // Returns for example "2 pcs" or "0.5 kg".
    public static String formatWithUnit(double quantity, String unit) {
        return format(quantity) + " " + unit;
    }
}

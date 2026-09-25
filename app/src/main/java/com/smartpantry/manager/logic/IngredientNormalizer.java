package com.smartpantry.manager.logic;

import java.util.Locale;

// Turns an ingredient name into a comparable form, so "  Tomatoes " and "tomato"

public final class IngredientNormalizer {

    private IngredientNormalizer() {
    }

    // Lowercase, strip punctuation, collapse spaces, then singularise the last word.
    public static String normalize(String name) {
        if (name == null) {
            return "";
        }

        String cleaned = name.trim().toLowerCase(Locale.ROOT);

        StringBuilder letters = new StringBuilder(cleaned.length());
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            if (Character.isLetterOrDigit(c) || c == ' ') {
                letters.append(c);
            }
        }

        String[] words = letters.toString().trim().split("\\s+");
        if (words.length == 0 || words[0].isEmpty()) {
            return "";
        }

        // Only the last word carries the plural: "spring onions" -> "spring onion".
        words[words.length - 1] = singularise(words[words.length - 1]);

        return String.join(" ", words);
    }

    // A deliberately small rule set
    private static String singularise(String word) {
        if (word.length() <= 3) {
            return word;
        }
        // berries -> berry
        if (word.endsWith("ies") && word.length() > 4) {
            return word.substring(0, word.length() - 3) + "y";
        }
        // tomatoes -> tomato, dishes -> dish, boxes -> box
        if (word.endsWith("oes") || word.endsWith("ches") || word.endsWith("shes")
                || word.endsWith("sses") || word.endsWith("xes") || word.endsWith("zes")) {
            return word.substring(0, word.length() - 2);
        }
        // eggs -> egg, but leave grass, hummus and axis alone
        if (word.endsWith("s") && !word.endsWith("ss") && !word.endsWith("us") && !word.endsWith("is")) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }
}

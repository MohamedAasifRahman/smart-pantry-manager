package com.smartpantry.manager.logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// Resolves a unit string to a family and converts quantities to that family's base unit

public final class UnitConverter {

    // slice and clove are separate from COUNT on purpose

    public enum Family {MASS, VOLUME, COUNT, SLICE, CLOVE, OTHER}

    private static final Map<String, Unit> KNOWN_UNITS = buildKnownUnits();

    private UnitConverter() {
    }

    // Looks up a unit. An unrecognised one becomes its own family

    public static Unit resolve(String unit) {
        if (unit == null) {
            return new Unit(Family.OTHER, "", 1d);
        }
        String key = unit.trim().toLowerCase(Locale.ROOT);
        Unit known = KNOWN_UNITS.get(key);
        return known != null ? known : new Unit(Family.OTHER, key, 1d);
    }

    public static final class Unit {

        private final Family family;
        private final String key;
        private final double toBaseFactor;

        private Unit(Family family, String key, double toBaseFactor) {
            this.family = family;
            this.key = key;
            this.toBaseFactor = toBaseFactor;
        }

        public Family getFamily() {
            return family;
        }

        // Converts to grams, millilitres, pieces, slices or cloves.
        public double toBase(double quantity) {
            return quantity * toBaseFactor;
        }

        // Two units can only be compared inside the same family

        // Unrecognised units must also be spelled identically.
        public boolean isCompatibleWith(Unit other) {
            if (other == null || family != other.family) {
                return false;
            }
            if (family == Family.OTHER) {
                return key.equals(other.key);
            }
            return true;
        }
    }

    private static Map<String, Unit> buildKnownUnits() {
        Map<String, Unit> units = new HashMap<>();

        // Mass, base unit gram
        put(units, Family.MASS, 0.001d, "mg");
        put(units, Family.MASS, 1d, "g", "gram", "grams");
        put(units, Family.MASS, 1000d, "kg", "kilogram", "kilograms");

        // Volume, base unit millilitre. Spoon and cup sizes are the metric ones.
        put(units, Family.VOLUME, 1d, "ml", "millilitre", "millilitres", "milliliter", "milliliters");
        put(units, Family.VOLUME, 1000d, "l", "litre", "litres", "liter", "liters");
        put(units, Family.VOLUME, 5d, "tsp", "teaspoon", "teaspoons");
        put(units, Family.VOLUME, 15d, "tbsp", "tablespoon", "tablespoons");
        put(units, Family.VOLUME, 250d, "cup", "cups");

        // Counted items
        put(units, Family.COUNT, 1d, "pc", "pcs", "piece", "pieces", "unit", "units");
        put(units, Family.SLICE, 1d, "slice", "slices");
        put(units, Family.CLOVE, 1d, "clove", "cloves");

        return Collections.unmodifiableMap(units);
    }

    private static void put(Map<String, Unit> units, Family family, double factor, String... spellings) {
        for (String spelling : spellings) {
            units.put(spelling, new Unit(family, spelling, factor));
        }
    }
}

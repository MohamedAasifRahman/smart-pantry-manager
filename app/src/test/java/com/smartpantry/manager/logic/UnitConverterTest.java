package com.smartpantry.manager.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UnitConverterTest {

    private static final double DELTA = 0.0001d;

    @Test
    public void convertsMassToGrams() {
        assertEquals(1000d, UnitConverter.resolve("kg").toBase(1d), DELTA);
        assertEquals(50d, UnitConverter.resolve("g").toBase(50d), DELTA);
        assertEquals(50d, UnitConverter.resolve("kg").toBase(0.05d), DELTA);
    }

    @Test
    public void convertsVolumeToMillilitres() {
        assertEquals(100d, UnitConverter.resolve("l").toBase(0.1d), DELTA);
        assertEquals(15d, UnitConverter.resolve("tbsp").toBase(1d), DELTA);
        assertEquals(250d, UnitConverter.resolve("cup").toBase(1d), DELTA);
    }

    @Test
    public void unitsInTheSameFamilyAreComparable() {
        assertTrue(UnitConverter.resolve("kg").isCompatibleWith(UnitConverter.resolve("g")));
        assertTrue(UnitConverter.resolve("l").isCompatibleWith(UnitConverter.resolve("ml")));
    }

    // The example from the brief: 500 g of flour is not 500 ml of flour.
    @Test
    public void massIsNeverComparedWithVolume() {
        assertFalse(UnitConverter.resolve("g").isCompatibleWith(UnitConverter.resolve("ml")));
    }

    @Test
    public void sliceAndCloveAreTheirOwnFamilies() {
        assertFalse(UnitConverter.resolve("slice").isCompatibleWith(UnitConverter.resolve("pcs")));
        assertFalse(UnitConverter.resolve("clove").isCompatibleWith(UnitConverter.resolve("pcs")));
        assertTrue(UnitConverter.resolve("slice").isCompatibleWith(UnitConverter.resolve("slices")));
    }

    @Test
    public void spellingVariationsResolveToTheSameFamily() {
        assertTrue(UnitConverter.resolve("GRAMS").isCompatibleWith(UnitConverter.resolve(" g ")));
        assertEquals(UnitConverter.Family.COUNT, UnitConverter.resolve("pieces").getFamily());
    }

    @Test
    public void unknownUnitsOnlyMatchThemselves() {
        assertTrue(UnitConverter.resolve("packet").isCompatibleWith(UnitConverter.resolve("packet")));
        assertFalse(UnitConverter.resolve("packet").isCompatibleWith(UnitConverter.resolve("g")));
        assertFalse(UnitConverter.resolve("packet").isCompatibleWith(UnitConverter.resolve("box")));
    }
}

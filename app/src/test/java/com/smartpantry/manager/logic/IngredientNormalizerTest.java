package com.smartpantry.manager.logic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class IngredientNormalizerTest {

    @Test
    public void lowercasesAndTrims() {
        assertEquals("tomato", IngredientNormalizer.normalize("  Tomato  "));
        assertEquals("tomato", IngredientNormalizer.normalize("TOMATO"));
    }


    @Test
    public void treatsTomatoAndTomatoesAsTheSame() {
        assertEquals(
                IngredientNormalizer.normalize("tomato"),
                IngredientNormalizer.normalize("  Tomatoes "));
    }

    @Test
    public void singularisesCommonPlurals() {
        assertEquals("egg", IngredientNormalizer.normalize("Eggs"));
        assertEquals("onion", IngredientNormalizer.normalize("onions"));
        assertEquals("potato", IngredientNormalizer.normalize("Potatoes"));
        assertEquals("berry", IngredientNormalizer.normalize("berries"));
    }

    @Test
    public void leavesWordsThatOnlyLookPluralAlone() {
        assertEquals("cheese", IngredientNormalizer.normalize("Cheese"));
        assertEquals("hummus", IngredientNormalizer.normalize("hummus"));
        assertEquals("grass", IngredientNormalizer.normalize("grass"));
        assertEquals("rice", IngredientNormalizer.normalize("Rice"));
    }

    @Test
    public void onlyTheLastWordIsSingularised() {
        assertEquals("spring onion", IngredientNormalizer.normalize("Spring Onions"));
        assertEquals("baked bean", IngredientNormalizer.normalize("Baked Beans"));
    }

    @Test
    public void collapsesRepeatedSpacesAndDropsPunctuation() {
        assertEquals("cooking oil", IngredientNormalizer.normalize("Cooking    Oil"));
        assertEquals("tomato", IngredientNormalizer.normalize("tomato,"));
    }

    @Test
    public void handlesNullAndBlank() {
        assertEquals("", IngredientNormalizer.normalize(null));
        assertEquals("", IngredientNormalizer.normalize("   "));
    }
}

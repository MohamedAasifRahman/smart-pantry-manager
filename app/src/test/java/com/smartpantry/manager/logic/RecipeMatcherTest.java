package com.smartpantry.manager.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.smartpantry.manager.model.Ingredient;
import com.smartpantry.manager.model.Recipe;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// The worked examples from the assignment brief, written as tests.
public class RecipeMatcherTest {

    // The recipe used throughout: 2 eggs, 1 tomato, 1 onion, 50 g cheese.
    private Recipe omelette() {
        return new Recipe("Cheese Omelette", "Breakfast", 10, "Beat. Fry. Fold.")
                .requires("eggs", 2, "pcs")
                .requires("tomato", 1, "pcs")
                .requires("onion", 1, "pcs")
                .requires("cheese", 50, "g");
    }

    private List<Ingredient> pantryWithoutCheese() {
        return new ArrayList<>(Arrays.asList(
                new Ingredient("Eggs", 2, "pcs", null),
                new Ingredient("Tomato", 1, "pcs", null),
                new Ingredient("Onion", 1, "pcs", null)));
    }

    private List<Ingredient> pantryWithCheese(double amount, String unit) {
        List<Ingredient> pantry = pantryWithoutCheese();
        pantry.add(new Ingredient("Cheese", amount, unit, null));
        return pantry;
    }

    @Test
    public void everyIngredientPresentInEnoughQuantityMatches() {
        assertTrue(new RecipeMatcher(pantryWithCheese(50, "g")).canMake(omelette()));
    }

    // One missing ingredient must exclude the recipe entirely.
    @Test
    public void oneMissingIngredientFails() {
        assertFalse(new RecipeMatcher(pantryWithoutCheese()).canMake(omelette()));
    }

    // 30 g of cheese when 50 g is required is still a failure.
    @Test
    public void insufficientQuantityFails() {
        assertFalse(new RecipeMatcher(pantryWithCheese(30, "g")).canMake(omelette()));
    }

    @Test
    public void exactlyEnoughIsEnough() {
        assertTrue(new RecipeMatcher(pantryWithCheese(50, "g")).canMake(omelette()));
    }

    // 0.05 kg is 50 g, so the same unit family converts correctly.
    @Test
    public void compatibleUnitsAreConverted() {
        assertTrue(new RecipeMatcher(pantryWithCheese(0.05, "kg")).canMake(omelette()));
    }

    // 50 ml of cheese is not 50 g of cheese.
    @Test
    public void incompatibleUnitsDoNotCount() {
        assertFalse(new RecipeMatcher(pantryWithCheese(50, "ml")).canMake(omelette()));
    }

    @Test
    public void pluralsAndWhitespaceStillMatch() {
        List<Ingredient> pantry = new ArrayList<>(Arrays.asList(
                new Ingredient("  Eggs  ", 2, "pcs", null),
                new Ingredient("Tomatoes", 1, "pcs", null),
                new Ingredient("ONIONS", 1, "pcs", null),
                new Ingredient("cheese", 50, "g", null)));
        assertTrue(new RecipeMatcher(pantry).canMake(omelette()));
    }

    // Two rows of the same ingredient add up.
    @Test
    public void duplicateRowsOfTheSameIngredientAreSummed() {
        List<Ingredient> pantry = new ArrayList<>(Arrays.asList(
                new Ingredient("Eggs", 1, "pcs", null),
                new Ingredient("Eggs", 1, "pcs", null),
                new Ingredient("Tomato", 1, "pcs", null),
                new Ingredient("Onion", 1, "pcs", null),
                new Ingredient("Cheese", 50, "g", null)));
        assertTrue(new RecipeMatcher(pantry).canMake(omelette()));
    }

    // Only the rows in a comparable unit contribute.
    @Test
    public void incompatibleDuplicateRowsAreIgnoredInTheSum() {
        List<Ingredient> pantry = new ArrayList<>(Arrays.asList(
                new Ingredient("Eggs", 2, "pcs", null),
                new Ingredient("Tomato", 1, "pcs", null),
                new Ingredient("Onion", 1, "pcs", null),
                new Ingredient("Cheese", 30, "g", null),
                new Ingredient("Cheese", 100, "ml", null)));
        assertFalse(new RecipeMatcher(pantry).canMake(omelette()));
    }

    @Test
    public void anEmptyPantryMatchesNothing() {
        assertFalse(new RecipeMatcher(new ArrayList<>()).canMake(omelette()));
        assertFalse(new RecipeMatcher(null).canMake(omelette()));
    }

    @Test
    public void findMatchingRecipesReturnsOnlyTheQualifyingOnes() {
        Recipe toast = new Recipe("Toast", "Breakfast", 3, "Toast it.")
                .requires("bread", 2, "slice");

        List<Ingredient> pantry = pantryWithCheese(50, "g");
        pantry.add(new Ingredient("Bread", 1, "slice", null));   // one slice short

        List<Recipe> matches = new RecipeMatcher(pantry)
                .findMatchingRecipes(Arrays.asList(omelette(), toast));

        assertEquals(1, matches.size());
        assertEquals("Cheese Omelette", matches.get(0).getName());
    }
}

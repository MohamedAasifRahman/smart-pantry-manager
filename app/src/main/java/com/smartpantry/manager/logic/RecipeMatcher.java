package com.smartpantry.manager.logic;

import com.smartpantry.manager.model.Ingredient;
import com.smartpantry.manager.model.Recipe;
import com.smartpantry.manager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// The strict matching rule

public class RecipeMatcher {

    // Absorbs floating point error: 0.1 litres converts to 100.00000000000001 ml.
    private static final double EPSILON = 0.000001d;

    // Pantry indexed by normalised name, built once so matching is a hash lookup

    private final Map<String, List<Ingredient>> pantryIndex;

    public RecipeMatcher(List<Ingredient> pantry) {
        this.pantryIndex = new HashMap<>();
        if (pantry == null) {
            return;
        }
        for (Ingredient item : pantry) {
            String key = IngredientNormalizer.normalize(item.getName());
            List<Ingredient> sameName = pantryIndex.get(key);
            if (sameName == null) {
                sameName = new ArrayList<>();
                pantryIndex.put(key, sameName);
            }
            sameName.add(item);
        }
    }

    // True only when the pantry covers every required ingredient.
    public boolean canMake(Recipe recipe) {
        if (recipe == null || recipe.getIngredients().isEmpty()) {
            return false;
        }

        for (RecipeIngredient required : recipe.getIngredients()) {

            String key = IngredientNormalizer.normalize(required.getName());
            UnitConverter.Unit requiredUnit = UnitConverter.resolve(required.getUnit());
            double requiredBase = requiredUnit.toBase(required.getQuantity());

            double availableBase = 0d;
            List<Ingredient> candidates = pantryIndex.get(key);
            if (candidates != null) {
                for (Ingredient item : candidates) {
                    UnitConverter.Unit itemUnit = UnitConverter.resolve(item.getUnit());
                    // Never compare grams with millilitres

                    if (!itemUnit.isCompatibleWith(requiredUnit)) {
                        continue;
                    }
                    // Two rows of the same ingredient add up.
                    availableBase += itemUnit.toBase(item.getQuantity());
                }
            }

            // Missing entirely, or present but not enough: the recipe fails here.
            if (availableBase + EPSILON < requiredBase) {
                return false;
            }
        }

        return true;
    }

    // Filters a list down to the recipes the user can make right now.
    public List<Recipe> findMatchingRecipes(List<Recipe> recipes) {
        List<Recipe> matches = new ArrayList<>();
        if (recipes == null) {
            return matches;
        }
        for (Recipe recipe : recipes) {
            if (canMake(recipe)) {
                matches.add(recipe);
            }
        }
        return matches;
    }
}

package com.smartpantry.manager.database;

import androidx.annotation.NonNull;

import com.smartpantry.manager.model.Ingredient;

import java.util.ArrayList;
import java.util.List;


// The five ingredients used by the testing plan and demonstration

public final class SampleData {

    private SampleData() {
        // Utility class, never instantiated.
    }

    @NonNull
    public static List<Ingredient> buildSamplePantry() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Eggs", 4, "pcs", null));
        ingredients.add(new Ingredient("Tomato", 2, "pcs", null));
        ingredients.add(new Ingredient("Onion", 1, "pcs", null));
        ingredients.add(new Ingredient("Cooking oil", 100, "ml", null));
        ingredients.add(new Ingredient("Salt", 50, "g", null));
        return ingredients;
    }
}

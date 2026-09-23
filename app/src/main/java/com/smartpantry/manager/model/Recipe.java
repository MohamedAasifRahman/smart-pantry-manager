package com.smartpantry.manager.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Recipe {

    private long id;
    private String name;
    private String category;
    private int prepMinutes;
    private String steps;
    private final List<RecipeIngredient> ingredients = new ArrayList<>();

    // Creates an unsaved recipe, used by the seed data.
    public Recipe(String name, String category, int prepMinutes, String steps) {
        this(Ingredient.NO_ID, name, category, prepMinutes, steps);
    }

    public Recipe(long id, String name, String category, int prepMinutes, String steps) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.prepMinutes = prepMinutes;
        this.steps = steps;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPrepMinutes() {
        return prepMinutes;
    }

    public void setPrepMinutes(int prepMinutes) {
        this.prepMinutes = prepMinutes;
    }


    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }


    public List<String> getStepList() {
        List<String> stepList = new ArrayList<>();
        if (steps == null) {
            return stepList;
        }
        for (String line : steps.split("\n")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                stepList.add(trimmed);
            }
        }
        return stepList;
    }


    public List<RecipeIngredient> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    public void addIngredient(RecipeIngredient ingredient) {
        ingredients.add(ingredient);
    }


    public Recipe requires(String name, double quantity, String unit) {
        addIngredient(new RecipeIngredient(name, quantity, unit));
        return this;
    }

    public int getIngredientCount() {
        return ingredients.size();
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}

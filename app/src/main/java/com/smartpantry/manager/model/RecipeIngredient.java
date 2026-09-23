package com.smartpantry.manager.model;

import androidx.annotation.NonNull;

public class RecipeIngredient {

    private long id;
    private long recipeId;
    private String name;
    private double quantity;
    private String unit;

    // Creates a required ingredient that has not been saved yet
    public RecipeIngredient(String name, double quantity, String unit) {
        this(Ingredient.NO_ID, Ingredient.NO_ID, name, quantity, unit);
    }

    public RecipeIngredient(long id, long recipeId, String name, double quantity, String unit) {
        this.id = id;
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @NonNull
    @Override
    public String toString() {
        return name + " " + quantity + " " + unit;
    }
}

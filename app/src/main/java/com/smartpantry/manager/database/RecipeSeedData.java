package com.smartpantry.manager.database;

import com.smartpantry.manager.model.Recipe;

import java.util.ArrayList;
import java.util.List;

// The 20 recipes written to the database once on first run.

public final class RecipeSeedData {

    public static final String CATEGORY_BREAKFAST = "Breakfast";
    public static final String CATEGORY_LUNCH = "Lunch";
    public static final String CATEGORY_DINNER = "Dinner";
    public static final String CATEGORY_SNACK = "Snack";

    private RecipeSeedData() {
    }

    // The full seed collection, ready to be inserted.
    public static List<Recipe> buildSeedRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        recipes.add(new Recipe("Tomato Omelette", CATEGORY_BREAKFAST, 10,
                "Chop the tomato and onion finely.\n"
                        + "Beat the eggs with the salt.\n"
                        + "Fry the onion and tomato in the oil for two minutes.\n"
                        + "Pour in the eggs, cook until set, then fold over.")
                .requires("eggs", 2, "pcs")
                .requires("tomato", 1, "pcs")
                .requires("onion", 1, "pcs")
                .requires("cooking oil", 10, "ml")
                .requires("salt", 2, "g"));

        recipes.add(new Recipe("Cheese Omelette", CATEGORY_BREAKFAST, 10,
                "Beat the eggs with the salt.\n"
                        + "Melt the butter in a pan over medium heat.\n"
                        + "Pour in the eggs and cook until almost set.\n"
                        + "Scatter the cheese over, fold and serve.")
                .requires("eggs", 3, "pcs")
                .requires("cheese", 40, "g")
                .requires("butter", 10, "g")
                .requires("salt", 2, "g"));

        recipes.add(new Recipe("Scrambled Eggs on Toast", CATEGORY_BREAKFAST, 10,
                "Beat the eggs with the milk and salt.\n"
                        + "Melt the butter in a pan over low heat.\n"
                        + "Add the eggs and stir gently until just set.\n"
                        + "Toast the bread and spoon the eggs on top.")
                .requires("eggs", 2, "pcs")
                .requires("milk", 30, "ml")
                .requires("butter", 10, "g")
                .requires("bread", 2, "slice")
                .requires("salt", 2, "g"));

        recipes.add(new Recipe("Avocado Toast", CATEGORY_BREAKFAST, 7,
                "Toast the bread until golden.\n"
                        + "Mash the avocado with the salt and pepper.\n"
                        + "Spread the avocado over the toast.")
                .requires("bread", 2, "slice")
                .requires("avocado", 1, "pcs")
                .requires("salt", 1, "g")
                .requires("black pepper", 1, "g"));

        recipes.add(new Recipe("Banana Oat Bowl", CATEGORY_BREAKFAST, 5,
                "Warm the milk and stir in the oats.\n"
                        + "Let it stand for three minutes until thick.\n"
                        + "Slice the banana over the top and drizzle with honey.")
                .requires("oats", 60, "g")
                .requires("milk", 250, "ml")
                .requires("banana", 1, "pcs")
                .requires("honey", 15, "g"));

        recipes.add(new Recipe("Peanut Butter Banana Toast", CATEGORY_SNACK, 5,
                "Toast the bread.\n"
                        + "Spread the peanut butter over both slices.\n"
                        + "Slice the banana on top.")
                .requires("bread", 2, "slice")
                .requires("peanut butter", 30, "g")
                .requires("banana", 1, "pcs"));

        recipes.add(new Recipe("Yoghurt and Apple Cup", CATEGORY_SNACK, 5,
                "Dice the apple.\n"
                        + "Spoon the yoghurt into a cup.\n"
                        + "Add the apple and drizzle with honey.")
                .requires("yoghurt", 200, "ml")
                .requires("apple", 1, "pcs")
                .requires("honey", 10, "g"));

        recipes.add(new Recipe("Spinach and Feta Scramble", CATEGORY_BREAKFAST, 12,
                "Heat the olive oil and wilt the spinach for a minute.\n"
                        + "Beat the eggs and pour them in.\n"
                        + "Stir gently until just set.\n"
                        + "Crumble the feta over and serve.")
                .requires("eggs", 3, "pcs")
                .requires("spinach", 80, "g")
                .requires("feta cheese", 50, "g")
                .requires("olive oil", 10, "ml"));

        recipes.add(new Recipe("Toasted Cheese and Tomato", CATEGORY_LUNCH, 10,
                "Butter the outside of each slice of bread.\n"
                        + "Layer the cheese and sliced tomato between the slices.\n"
                        + "Toast in a pan until golden on both sides.")
                .requires("bread", 4, "slice")
                .requires("cheese", 60, "g")
                .requires("tomato", 1, "pcs")
                .requires("butter", 20, "g"));

        recipes.add(new Recipe("Tuna Salad Sandwich", CATEGORY_LUNCH, 10,
                "Drain the tuna and mix it with the mayonnaise.\n"
                        + "Lay the lettuce on two slices of bread.\n"
                        + "Spoon the tuna over and close the sandwiches.")
                .requires("bread", 4, "slice")
                .requires("tuna", 150, "g")
                .requires("mayonnaise", 30, "g")
                .requires("lettuce", 30, "g"));

        recipes.add(new Recipe("Tuna Pasta", CATEGORY_LUNCH, 20,
                "Boil the pasta until tender, then drain.\n"
                        + "Finely chop the onion.\n"
                        + "Mix the tuna, mayonnaise, onion and pepper into the warm pasta.")
                .requires("pasta", 200, "g")
                .requires("tuna", 150, "g")
                .requires("mayonnaise", 40, "g")
                .requires("onion", 1, "pcs")
                .requires("black pepper", 1, "g"));

        recipes.add(new Recipe("Chicken Wrap", CATEGORY_LUNCH, 15,
                "Cook and slice the chicken breast.\n"
                        + "Warm the wraps in a dry pan.\n"
                        + "Spread the mayonnaise, then add the lettuce, tomato and chicken.\n"
                        + "Roll up tightly and cut in half.")
                .requires("tortilla wrap", 2, "pcs")
                .requires("chicken breast", 200, "g")
                .requires("lettuce", 40, "g")
                .requires("tomato", 1, "pcs")
                .requires("mayonnaise", 30, "g"));

        recipes.add(new Recipe("Greek-Style Salad", CATEGORY_LUNCH, 10,
                "Chop the cucumber, tomato and onion.\n"
                        + "Toss them together in a bowl.\n"
                        + "Crumble the feta over and dress with the olive oil.")
                .requires("cucumber", 1, "pcs")
                .requires("tomato", 2, "pcs")
                .requires("onion", 1, "pcs")
                .requires("feta cheese", 80, "g")
                .requires("olive oil", 20, "ml"));

        recipes.add(new Recipe("Garden Pasta", CATEGORY_LUNCH, 20,
                "Boil the pasta until tender, then drain.\n"
                        + "Fry the crushed garlic in the olive oil for a minute.\n"
                        + "Add the chopped tomato and herbs and simmer for five minutes.\n"
                        + "Stir the sauce through the pasta.")
                .requires("pasta", 200, "g")
                .requires("tomato", 3, "pcs")
                .requires("garlic", 2, "clove")
                .requires("olive oil", 20, "ml")
                .requires("mixed herbs", 3, "g"));

        recipes.add(new Recipe("Creamy Mushroom Pasta", CATEGORY_DINNER, 25,
                "Boil the pasta until tender, then drain.\n"
                        + "Slice the mushrooms and fry them in the butter with the garlic.\n"
                        + "Pour in the milk and simmer until slightly thickened.\n"
                        + "Toss the pasta through the sauce.")
                .requires("pasta", 200, "g")
                .requires("mushroom", 200, "g")
                .requires("milk", 150, "ml")
                .requires("butter", 20, "g")
                .requires("garlic", 2, "clove"));

        recipes.add(new Recipe("Chicken Rice Bowl", CATEGORY_DINNER, 30,
                "Cook the rice until tender.\n"
                        + "Dice the chicken and fry it in the oil until cooked through.\n"
                        + "Add the diced carrot and peas and cook for five minutes.\n"
                        + "Spoon the chicken and vegetables over the rice.")
                .requires("rice", 200, "g")
                .requires("chicken breast", 250, "g")
                .requires("carrot", 1, "pcs")
                .requires("peas", 80, "g")
                .requires("cooking oil", 15, "ml"));

        recipes.add(new Recipe("Vegetable Fried Rice", CATEGORY_DINNER, 20,
                "Cook the rice and let it cool slightly.\n"
                        + "Heat the oil and scramble the eggs, then set them aside.\n"
                        + "Fry the diced carrot and peas for three minutes.\n"
                        + "Add the rice and egg, and toss everything together.")
                .requires("rice", 250, "g")
                .requires("eggs", 2, "pcs")
                .requires("carrot", 1, "pcs")
                .requires("peas", 100, "g")
                .requires("cooking oil", 20, "ml"));

        recipes.add(new Recipe("Potato and Egg Hash", CATEGORY_DINNER, 25,
                "Dice the potatoes and onion.\n"
                        + "Fry them in the oil with the salt until golden and soft.\n"
                        + "Make two hollows, crack in the eggs and cover until set.")
                .requires("potato", 3, "pcs")
                .requires("eggs", 2, "pcs")
                .requires("onion", 1, "pcs")
                .requires("cooking oil", 20, "ml")
                .requires("salt", 2, "g"));

        recipes.add(new Recipe("Pap and Tomato Relish", CATEGORY_DINNER, 30,
                "Boil salted water and stir in the maize meal.\n"
                        + "Cover and steam on low heat for twenty minutes.\n"
                        + "Fry the chopped onion and tomato in the oil until soft.\n"
                        + "Serve the relish over the pap.")
                .requires("maize meal", 250, "g")
                .requires("tomato", 3, "pcs")
                .requires("onion", 1, "pcs")
                .requires("cooking oil", 15, "ml")
                .requires("salt", 3, "g"));

        recipes.add(new Recipe("Beans and Carrot Curry", CATEGORY_DINNER, 25,
                "Fry the chopped onion in the oil until soft.\n"
                        + "Stir in the curry powder and cook for a minute.\n"
                        + "Add the grated carrot and cook for five minutes.\n"
                        + "Stir in the beans and simmer for ten minutes.")
                .requires("baked beans", 400, "g")
                .requires("carrot", 2, "pcs")
                .requires("onion", 1, "pcs")
                .requires("curry powder", 5, "g")
                .requires("cooking oil", 15, "ml"));

        return recipes;
    }
}

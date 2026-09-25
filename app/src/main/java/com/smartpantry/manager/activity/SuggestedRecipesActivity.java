package com.smartpantry.manager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.manager.R;
import com.smartpantry.manager.adapter.RecipeAdapter;
import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.logic.RecipeMatcher;
import com.smartpantry.manager.model.Ingredient;
import com.smartpantry.manager.model.Recipe;
import com.smartpantry.manager.util.IntentKeys;

import java.util.List;


// Shows only the recipes the user can make right now

public class SuggestedRecipesActivity extends BaseNavigationActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private DatabaseHelper databaseHelper;
    private RecipeAdapter recipeAdapter;

    private RecyclerView recyclerView;
    private View emptyStateView;
    private TextView countLabel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recycler_recipes);
        emptyStateView = findViewById(R.id.layout_empty_recipes);
        countLabel = findViewById(R.id.text_recipes_count);

        recipeAdapter = new RecipeAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recipeAdapter);

        setUpBottomNavigation(R.id.nav_recipes);
    }

    // Recalculated on every return to this screen, editing the pantry and coming back shows the new answer immediately
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        List<Ingredient> pantry = databaseHelper.getAllIngredients();
        List<Recipe> allRecipes = databaseHelper.getAllRecipes();

        // The matcher indexes the pantry once, then filters the recipes
        List<Recipe> matches = new RecipeMatcher(pantry).findMatchingRecipes(allRecipes);

        recipeAdapter.setRecipes(matches);
        showListOrEmptyState(matches.size());
    }

    // A pantry that matches nothing gets a clear message, never a blank screen.
    private void showListOrEmptyState(int matchCount) {
        boolean hasMatches = matchCount > 0;
        recyclerView.setVisibility(hasMatches ? View.VISIBLE : View.GONE);
        emptyStateView.setVisibility(hasMatches ? View.GONE : View.VISIBLE);

        if (matchCount == 1) {
            countLabel.setText(R.string.recipes_count_one);
        } else {
            countLabel.setText(getString(R.string.recipes_count_many, matchCount));
        }
    }

    // Only the id travels to the detail screen, which loads the recipe itself.
    @Override
    public void onRecipeClicked(@NonNull Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(IntentKeys.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}

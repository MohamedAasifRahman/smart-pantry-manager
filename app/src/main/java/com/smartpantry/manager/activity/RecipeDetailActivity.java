package com.smartpantry.manager.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.smartpantry.manager.R;
import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.model.Ingredient;
import com.smartpantry.manager.model.Recipe;
import com.smartpantry.manager.model.RecipeIngredient;
import com.smartpantry.manager.util.IntentKeys;
import com.smartpantry.manager.util.QuantityFormatter;

import java.util.List;


// Shows one recipe in full

// The Intent carries only the recipe id; everything displayed is read from the database

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_recipe_detail);
        toolbar.setNavigationOnClickListener(view -> finish());

        long recipeId = getIntent().getLongExtra(IntentKeys.EXTRA_RECIPE_ID, Ingredient.NO_ID);

        // This screen reads the recipe once and never touches the database again,
        // so try-with-resources closes the helper as soon as the read is done
        Recipe recipe;
        try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
            recipe = databaseHelper.getRecipeById(recipeId);
        }

        if (recipe == null) {
            // No id, or the recipe is gone. Nothing to show
            finish();
            return;
        }

        bindRecipe(recipe);
    }

    private void bindRecipe(@NonNull Recipe recipe) {
        TextView nameView = findViewById(R.id.text_detail_name);
        TextView metaView = findViewById(R.id.text_detail_meta);

        nameView.setText(recipe.getName());
        metaView.setText(getString(R.string.recipe_meta, recipe.getCategory(), recipe.getPrepMinutes()));

        addIngredientRows(recipe.getIngredients());
        addNumberedSteps(recipe.getStepList());
    }

    // One inflated row per required ingredient, showing the quantity and unit

    private void addIngredientRows(@NonNull List<RecipeIngredient> ingredients) {
        LinearLayout container = findViewById(R.id.container_ingredients);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (RecipeIngredient required : ingredients) {
            View row = inflater.inflate(R.layout.item_recipe_ingredient, container, false);

            TextView name = row.findViewById(R.id.text_required_name);
            TextView quantity = row.findViewById(R.id.text_required_quantity);

            name.setText(required.getName());
            quantity.setText(QuantityFormatter.formatWithUnit(required.getQuantity(), required.getUnit()));

            container.addView(row);
        }
    }

    // Steps are stored one per line and numbered here at display time
    private void addNumberedSteps(@NonNull List<String> steps) {
        LinearLayout container = findViewById(R.id.container_steps);
        int topMargin = getResources().getDimensionPixelSize(R.dimen.space_s);

        for (int i = 0; i < steps.size(); i++) {
            TextView stepView = new TextView(this);
            stepView.setText(getString(R.string.recipe_step, i + 1, steps.get(i)));
            stepView.setTextAppearance(R.style.TextAppearance_SmartPantry_Body);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = topMargin;
            stepView.setLayoutParams(params);

            container.addView(stepView);
        }
    }
}

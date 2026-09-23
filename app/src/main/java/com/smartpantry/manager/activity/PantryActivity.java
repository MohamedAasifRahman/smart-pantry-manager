package com.smartpantry.manager.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.smartpantry.manager.R;
import com.smartpantry.manager.adapter.PantryAdapter;
import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.model.Ingredient;

import java.util.List;


 // The pantry screen and the launcher Activity of Smart Pantry Manager.

public class PantryActivity extends AppCompatActivity
        implements PantryAdapter.OnIngredientActionListener {

    private DatabaseHelper databaseHelper;
    private PantryAdapter pantryAdapter;

    private RecyclerView recyclerView;
    private View emptyStateView;
    private TextView countLabel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        // The theme has no action bar of its own, so the Material toolbar in
        // the layout becomes this Activity's app bar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recycler_pantry);
        emptyStateView = findViewById(R.id.layout_empty_pantry);
        countLabel = findViewById(R.id.text_pantry_count);

        pantryAdapter = new PantryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(pantryAdapter);
    }

    // onResume runs every time the screen returns to the foreground, so the
    // list refreshes itself after the user comes back from any other screen.
    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    // Reads the pantry from the database and hands it to the adapter.
    private void loadPantry() {
        List<Ingredient> ingredients = databaseHelper.getAllIngredients();
        pantryAdapter.setIngredients(ingredients);
        showListOrEmptyState(ingredients.size());
    }

    // Exactly one of the list and the empty state is visible, so the screen is never blank.
    private void showListOrEmptyState(int ingredientCount) {
        boolean hasIngredients = ingredientCount > 0;
        recyclerView.setVisibility(hasIngredients ? View.VISIBLE : View.GONE);
        emptyStateView.setVisibility(hasIngredients ? View.GONE : View.VISIBLE);

        if (ingredientCount == 0) {
            countLabel.setText(R.string.pantry_count_zero);
        } else if (ingredientCount == 1) {
            countLabel.setText(R.string.pantry_count_one);
        } else {
            countLabel.setText(getString(R.string.pantry_count_many, ingredientCount));
        }
    }

    // Always confirm before anything is removed from the database.
    @Override
    public void onDeleteRequested(@NonNull Ingredient ingredient) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(getString(R.string.dialog_delete_message, ingredient.getName()))
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_delete, (dialog, which) -> deleteIngredient(ingredient))
                .show();
    }

    private void deleteIngredient(@NonNull Ingredient ingredient) {
        boolean deleted = databaseHelper.deleteIngredient(ingredient.getId());
        showMessage(deleted ? R.string.msg_ingredient_deleted : R.string.msg_delete_failed);
        loadPantry();
    }

    private void showMessage(@StringRes int messageResId) {
        Snackbar.make(findViewById(R.id.pantry_root), messageResId, Snackbar.LENGTH_SHORT).show();
    }
}

package com.smartpantry.manager.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.manager.R;
import com.smartpantry.manager.model.Recipe;

import java.util.ArrayList;
import java.util.List;

// Shows the recipes the user can make right now
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClicked(@NonNull Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public RecipeAdapter(@NonNull OnRecipeClickListener listener) {
        this.listener = listener;
    }

    // The whole list is recalculated whenever the pantry changes so there is no single changed row to report
    @SuppressLint("NotifyDataSetChanged")
    public void setRecipes(@NonNull List<Recipe> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(recipes.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    // Public because onCreateViewHolder hands one back to the RecyclerView.
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameView;
        private final TextView metaView;
        private final TextView ingredientCountView;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.text_recipe_name);
            metaView = itemView.findViewById(R.id.text_recipe_meta);
            ingredientCountView = itemView.findViewById(R.id.text_recipe_ingredient_count);
        }

        void bind(@NonNull Recipe recipe, @NonNull OnRecipeClickListener listener) {
            nameView.setText(recipe.getName());
            metaView.setText(itemView.getContext().getString(
                    R.string.recipe_meta, recipe.getCategory(), recipe.getPrepMinutes()));
            ingredientCountView.setText(itemView.getContext().getString(
                    R.string.recipe_ingredient_count, recipe.getIngredientCount()));

            itemView.setOnClickListener(view -> listener.onRecipeClicked(recipe));
        }
    }
}

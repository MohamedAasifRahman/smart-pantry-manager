package com.smartpantry.manager.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.manager.R;
import com.smartpantry.manager.model.Ingredient;
import com.smartpantry.manager.util.QuantityFormatter;

import java.util.ArrayList;
import java.util.List;

// Binds the pantry list to the RecyclerView.
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    // The adapter reports what the user tapped; the Activity decides what to do.
    public interface OnIngredientActionListener {

        // The row itself was tapped, meaning open this one for editing.
        void onEditRequested(@NonNull Ingredient ingredient);

        // The delete icon on the row was tapped.
        void onDeleteRequested(@NonNull Ingredient ingredient);
    }

    private final List<Ingredient> ingredients = new ArrayList<>();
    private final OnIngredientActionListener listener;

    public PantryAdapter(@NonNull OnIngredientActionListener listener) {
        this.listener = listener;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setIngredients(@NonNull List<Ingredient> newIngredients) {
        ingredients.clear();
        ingredients.addAll(newIngredients);
        notifyDataSetChanged();
    }

    // Called when the RecyclerView needs a new row view.
    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(itemView);
    }

    // Called when an existing row view must show a different ingredient.
    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        holder.bind(ingredients.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    // Holds one row's views so they are looked up once instead of on every scroll frame.

    // Public because onCreateViewHolder hands one back to the RecyclerView.
    public static class PantryViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameView;
        private final TextView quantityView;
        private final TextView expiryView;
        private final ImageButton deleteButton;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.text_ingredient_name);
            quantityView = itemView.findViewById(R.id.text_ingredient_quantity);
            expiryView = itemView.findViewById(R.id.text_ingredient_expiry);
            deleteButton = itemView.findViewById(R.id.button_delete_ingredient);
        }

        void bind(@NonNull Ingredient ingredient, @NonNull OnIngredientActionListener listener) {
            nameView.setText(ingredient.getName());
            quantityView.setText(
                    QuantityFormatter.formatWithUnit(ingredient.getQuantity(), ingredient.getUnit()));

            if (ingredient.hasExpiryDate()) {
                expiryView.setText(itemView.getContext()
                        .getString(R.string.expiry_on, ingredient.getExpiryDate()));
                expiryView.setVisibility(View.VISIBLE);
            } else {
                expiryView.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(view -> listener.onEditRequested(ingredient));
            deleteButton.setOnClickListener(view -> listener.onDeleteRequested(ingredient));
        }
    }
}

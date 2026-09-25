package com.smartpantry.manager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.manager.R;
import com.smartpantry.manager.logic.ExpiryStatus;
import com.smartpantry.manager.model.Ingredient;
import com.smartpantry.manager.util.DateUtils;
import com.smartpantry.manager.util.QuantityFormatter;
import com.smartpantry.manager.util.SettingsManager;

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

    // Start from the same defaults as Settings, until the Activity says otherwise.
    private boolean alertsEnabled = SettingsManager.DEFAULT_EXPIRY_ALERTS;
    private int thresholdDays = SettingsManager.DEFAULT_THRESHOLD_DAYS;

    public PantryAdapter(@NonNull OnIngredientActionListener listener) {
        this.listener = listener;
    }

    // Called just before setIngredients, which is what redraws the list.
    public void setExpiryAlertSettings(boolean enabled, int days) {
        alertsEnabled = enabled;
        thresholdDays = days;
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

    // Called when an existing row view must show a different ingredient
    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        ExpiryStatus status = ExpiryStatus.evaluate(
                ingredient.getExpiryDate(), alertsEnabled, thresholdDays);
        holder.bind(ingredient, status, listener);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    // Holds one row's views so they are looked up once instead of on every scroll frame

    public static class PantryViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameView;
        private final TextView quantityView;
        private final TextView expiryView;
        private final TextView badgeView;
        private final ImageButton deleteButton;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.text_ingredient_name);
            quantityView = itemView.findViewById(R.id.text_ingredient_quantity);
            expiryView = itemView.findViewById(R.id.text_ingredient_expiry);
            badgeView = itemView.findViewById(R.id.text_expiry_badge);
            deleteButton = itemView.findViewById(R.id.button_delete_ingredient);
        }

        void bind(@NonNull Ingredient ingredient,
                  @NonNull ExpiryStatus status,
                  @NonNull OnIngredientActionListener listener) {
            nameView.setText(ingredient.getName());
            quantityView.setText(
                    QuantityFormatter.formatWithUnit(ingredient.getQuantity(), ingredient.getUnit()));

            bindExpiry(ingredient, status);

            itemView.setOnClickListener(view -> listener.onEditRequested(ingredient));
            deleteButton.setOnClickListener(view -> listener.onDeleteRequested(ingredient));
        }

        // The plain date line and the coloured badge are separate
        private void bindExpiry(@NonNull Ingredient ingredient, @NonNull ExpiryStatus status) {
            Context context = itemView.getContext();

            if (ingredient.hasExpiryDate()) {
                expiryView.setText(context.getString(R.string.expiry_on,
                        DateUtils.formatForDisplay(ingredient.getExpiryDate())));
                expiryView.setVisibility(View.VISIBLE);
            } else {
                expiryView.setVisibility(View.GONE);
            }

            switch (status.getState()) {
                case EXPIRED:
                    showBadge(context.getString(R.string.badge_expired),
                            R.drawable.bg_badge_error, R.color.error);
                    break;
                case TODAY:
                    showBadge(context.getString(R.string.badge_expires_today),
                            R.drawable.bg_badge_amber, R.color.text_primary);
                    break;
                case SOON:
                    // A plurals resource, so "1 day" and "2 days" both read correctly
                    showBadge(context.getResources().getQuantityString(
                                    R.plurals.badge_expires_in_days,
                                    status.getDays(), status.getDays()),
                            R.drawable.bg_badge_amber, R.color.text_primary);
                    break;
                default:
                    // Rows are reused as the list scrolls
                    badgeView.setVisibility(View.GONE);
                    break;
            }
        }

        private void showBadge(@NonNull String text,
                               @DrawableRes int background,
                               @ColorRes int textColor) {
            badgeView.setText(text);
            badgeView.setBackgroundResource(background);
            badgeView.setTextColor(itemView.getContext().getColor(textColor));
            badgeView.setVisibility(View.VISIBLE);
        }
    }
}

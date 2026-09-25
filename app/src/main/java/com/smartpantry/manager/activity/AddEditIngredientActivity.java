package com.smartpantry.manager.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.smartpantry.manager.R;
import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.model.Ingredient;
import com.smartpantry.manager.util.DateUtils;
import com.smartpantry.manager.util.IntentKeys;
import com.smartpantry.manager.util.QuantityFormatter;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


// Adds a new pantry ingredient, or edits an existing one.


public class AddEditIngredientActivity extends AppCompatActivity {

    private static final int MAX_NAME_LENGTH = 40;
    private static final double MAX_QUANTITY = 100000d;

    private DatabaseHelper databaseHelper;

    // The row being edited, or Ingredient.NO_ID when adding.
    private long ingredientId = Ingredient.NO_ID;

    private TextInputLayout nameLayout;
    private TextInputLayout quantityLayout;
    private TextInputLayout unitLayout;
    private TextInputLayout expiryLayout;
    private TextInputEditText nameInput;
    private TextInputEditText quantityInput;
    private AutoCompleteTextView unitInput;
    private TextInputEditText expiryInput;

    private List<String> availableUnits;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        databaseHelper = new DatabaseHelper(this);
        ingredientId = getIntent().getLongExtra(IntentKeys.EXTRA_INGREDIENT_ID, Ingredient.NO_ID);

        bindViews();
        setUpToolbar();
        setUpUnitDropdown();
        setUpDatePicker();

        findViewById(R.id.button_save).setOnClickListener(view -> save());
        findViewById(R.id.button_clear_date).setOnClickListener(view -> {
            expiryInput.setText("");
            expiryLayout.setError(null);
        });

        if (isEditMode()) {
            loadExistingIngredient();
        }
    }

    private void bindViews() {
        nameLayout = findViewById(R.id.layout_name);
        quantityLayout = findViewById(R.id.layout_quantity);
        unitLayout = findViewById(R.id.layout_unit);
        expiryLayout = findViewById(R.id.layout_expiry);
        nameInput = findViewById(R.id.input_name);
        quantityInput = findViewById(R.id.input_quantity);
        unitInput = findViewById(R.id.input_unit);
        expiryInput = findViewById(R.id.input_expiry);
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(isEditMode() ? R.string.title_edit_ingredient : R.string.title_add_ingredient);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    // The user picks a unit from a fixed list instead of typing one
    private void setUpUnitDropdown() {
        availableUnits = Arrays.asList(getResources().getStringArray(R.array.units));
        unitInput.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, availableUnits));
    }

    private void setUpDatePicker() {
        expiryInput.setOnClickListener(view -> showDatePicker());
        expiryLayout.setEndIconOnClickListener(view -> showDatePicker());
    }

    private void showDatePicker() {
        Calendar startFrom = Calendar.getInstance();
        Date alreadyChosen = DateUtils.parse(textOf(expiryInput));
        if (alreadyChosen != null) {
            startFrom.setTime(alreadyChosen);
        }

        new DatePickerDialog(
                this,
                (picker, year, month, dayOfMonth) -> {
                    Calendar chosen = Calendar.getInstance();
                    chosen.set(year, month, dayOfMonth);
                    expiryInput.setText(DateUtils.format(chosen));
                    expiryLayout.setError(null);
                },
                startFrom.get(Calendar.YEAR),
                startFrom.get(Calendar.MONTH),
                startFrom.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // Fills the form with the row being edited, read fresh from the database.
    private void loadExistingIngredient() {
        Ingredient existing = databaseHelper.getIngredientById(ingredientId);
        if (existing == null) {
            // The row was deleted on another screen while this one was opening.
            finish();
            return;
        }
        nameInput.setText(existing.getName());
        quantityInput.setText(QuantityFormatter.format(existing.getQuantity()));
        unitInput.setText(existing.getUnit(), false);
        expiryInput.setText(existing.hasExpiryDate() ? existing.getExpiryDate() : "");
    }

    // Checks every rule before anything touches the database
    private boolean isFormValid() {
        clearErrors();
        boolean valid = true;

        String name = textOf(nameInput);
        if (name.isEmpty()) {
            nameLayout.setError(getString(R.string.error_name_required));
            valid = false;
        } else if (name.length() > MAX_NAME_LENGTH) {
            nameLayout.setError(getString(R.string.error_name_too_long));
            valid = false;
        }

        String quantityText = textOf(quantityInput);
        if (quantityText.isEmpty()) {
            quantityLayout.setError(getString(R.string.error_quantity_required));
            valid = false;
        } else {
            try {
                double quantity = Double.parseDouble(quantityText);
                if (quantity <= 0d) {
                    quantityLayout.setError(getString(R.string.error_quantity_zero));
                    valid = false;
                } else if (quantity > MAX_QUANTITY) {
                    quantityLayout.setError(getString(R.string.error_quantity_too_large));
                    valid = false;
                }
            } catch (NumberFormatException e) {
                quantityLayout.setError(getString(R.string.error_quantity_invalid));
                valid = false;
            }
        }

        String unit = textOf(unitInput);
        if (unit.isEmpty() || !availableUnits.contains(unit)) {
            unitLayout.setError(getString(R.string.error_unit_required));
            valid = false;
        }

        // The expiry date is optional, but if one was entered it must be real.
        String expiry = textOf(expiryInput);
        if (!expiry.isEmpty()) {
            if (!DateUtils.isValidDate(expiry)) {
                expiryLayout.setError(getString(R.string.error_date_invalid));
                valid = false;
            } else if (!DateUtils.isRealisticExpiry(expiry)) {
                expiryLayout.setError(getString(R.string.error_date_unrealistic));
                valid = false;
            }
        }

        return valid;
    }

    private void clearErrors() {
        nameLayout.setError(null);
        quantityLayout.setError(null);
        unitLayout.setError(null);
        expiryLayout.setError(null);
    }

    private void save() {
        if (!isFormValid()) {
            return;
        }

        String expiry = textOf(expiryInput);
        Ingredient ingredient = new Ingredient(
                ingredientId,
                textOf(nameInput),
                Double.parseDouble(textOf(quantityInput)),
                textOf(unitInput),
                expiry.isEmpty() ? null : expiry);

        boolean saved;
        int successMessage;
        if (isEditMode()) {
            saved = databaseHelper.updateIngredient(ingredient);
            successMessage = R.string.msg_ingredient_updated;
        } else {
            saved = databaseHelper.insertIngredient(ingredient) != Ingredient.NO_ID;
            successMessage = R.string.msg_ingredient_saved;
        }

        if (!saved) {
            nameLayout.setError(getString(R.string.msg_save_failed));
            return;
        }

        // The pantry screen shows the confirmation, because this screen is closing.

        Intent result = new Intent();
        result.putExtra(IntentKeys.EXTRA_RESULT_MESSAGE, successMessage);
        setResult(RESULT_OK, result);
        finish();
    }

    private boolean isEditMode() {
        return ingredientId != Ingredient.NO_ID;
    }

    private String textOf(TextView view) {
        return view.getText() == null ? "" : view.getText().toString().trim();
    }
}

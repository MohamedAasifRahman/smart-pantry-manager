package com.smartpantry.manager.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.smartpantry.manager.R;
import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.database.SampleData;
import com.smartpantry.manager.model.Ingredient;
import com.smartpantry.manager.util.SettingsManager;

import java.util.List;


// The settings screen
public class SettingsActivity extends BaseNavigationActivity {

    private SettingsManager settings;
    private DatabaseHelper databaseHelper;

    private SwitchMaterial alertsSwitch;
    private AutoCompleteTextView thresholdInput;
    private TextInputLayout thresholdLayout;
    private TextInputEditText nameInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        settings = new SettingsManager(this);
        databaseHelper = new DatabaseHelper(this);

        alertsSwitch = findViewById(R.id.switch_expiry_alerts);
        thresholdInput = findViewById(R.id.input_threshold);
        thresholdLayout = findViewById(R.id.layout_threshold);
        nameInput = findViewById(R.id.input_display_name);

        setUpExpiryAlerts();
        setUpThresholdDropdown();
        setUpDisplayName();
        setUpSampleData();
        showVersion();

        setUpBottomNavigation(R.id.nav_settings);
    }

    // The switch starts from the saved value
    private void setUpExpiryAlerts() {
        alertsSwitch.setChecked(settings.isExpiryAlertsEnabled());
        setThresholdEnabled(settings.isExpiryAlertsEnabled());

        alertsSwitch.setOnCheckedChangeListener((button, isChecked) -> {
            settings.setExpiryAlertsEnabled(isChecked);
            setThresholdEnabled(isChecked);
        });
    }

    // A threshold means nothing while the alerts are off

    private void setThresholdEnabled(boolean enabled) {
        thresholdLayout.setEnabled(enabled);
        thresholdInput.setEnabled(enabled);
    }

    private void setUpThresholdDropdown() {
        String[] labels = getResources().getStringArray(R.array.expiry_threshold_labels);
        thresholdInput.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labels));

        // false means "show this text without filtering the list".
        thresholdInput.setText(labels[settings.thresholdIndex()], false);

        thresholdInput.setOnItemClickListener((parent, view, position, id) ->
                settings.setExpiryThresholdDays(SettingsManager.thresholdForIndex(position)));
    }

    private void setUpDisplayName() {
        nameInput.setText(settings.getDisplayName());

        findViewById(R.id.button_save_name).setOnClickListener(view -> {
            String name = nameInput.getText() == null ? "" : nameInput.getText().toString().trim();
            settings.setDisplayName(name);
            showMessage(R.string.msg_display_name_saved);
        });
    }

    // Loading the sample pantry replaces what is there, so it always asks first.
    private void setUpSampleData() {
        findViewById(R.id.button_load_sample).setOnClickListener(view ->
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.dialog_load_sample_title)
                        .setMessage(R.string.dialog_load_sample_message)
                        .setNegativeButton(R.string.action_cancel, null)
                        .setPositiveButton(R.string.action_load, (dialog, which) -> loadSamplePantry())
                        .show());
    }

    // Clears the pantry, then inserts the five standard test ingredients.
    private void loadSamplePantry() {
        List<Ingredient> existing = databaseHelper.getAllIngredients();
        for (Ingredient ingredient : existing) {
            databaseHelper.deleteIngredient(ingredient.getId());
        }
        for (Ingredient sample : SampleData.buildSamplePantry()) {
            databaseHelper.insertIngredient(sample);
        }
        showMessage(R.string.msg_sample_loaded);
    }


    private void showVersion() {
        TextView versionLabel = findViewById(R.id.text_version);
        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            if (version == null) {
                versionLabel.setVisibility(View.GONE);
            } else {
                versionLabel.setText(getString(R.string.settings_version, version));
            }
        } catch (PackageManager.NameNotFoundException e) {
            versionLabel.setVisibility(View.GONE);
        }
    }

    private void showMessage(@StringRes int messageResId) {
        Snackbar.make(findViewById(R.id.settings_root), messageResId, Snackbar.LENGTH_SHORT).show();
    }
}

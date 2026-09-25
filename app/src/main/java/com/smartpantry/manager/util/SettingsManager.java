package com.smartpantry.manager.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;


// One place for every saved setting.

public class SettingsManager {

    private static final String PREFS_NAME = "smart_pantry_settings";

    private static final String KEY_EXPIRY_ALERTS = "pref_expiry_alerts_enabled";
    private static final String KEY_THRESHOLD_DAYS = "pref_expiry_threshold_days";
    private static final String KEY_DISPLAY_NAME = "pref_display_name";

    public static final boolean DEFAULT_EXPIRY_ALERTS = true;
    public static final int DEFAULT_THRESHOLD_DAYS = 3;

    // Same order as R.array.expiry_threshold_labels, so an index maps to a value.
    public static final int[] THRESHOLD_OPTIONS = {3, 7, 14};

    private final SharedPreferences preferences;

    public SettingsManager(@NonNull Context context) {
        // Application context, so a finishing Activity cannot leak through this object.
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Expiry alerts

    public boolean isExpiryAlertsEnabled() {
        return preferences.getBoolean(KEY_EXPIRY_ALERTS, DEFAULT_EXPIRY_ALERTS);
    }

    public void setExpiryAlertsEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
    }

    // How many days ahead an item counts as expiring soon

    public int getExpiryThresholdDays() {
        return preferences.getInt(KEY_THRESHOLD_DAYS, DEFAULT_THRESHOLD_DAYS);
    }

    public void setExpiryThresholdDays(int days) {
        preferences.edit().putInt(KEY_THRESHOLD_DAYS, days).apply();
    }

    // Display name shown in the pantry app bar

    @NonNull
    public String getDisplayName() {
        // The default is "", so this never comes back null
        return preferences.getString(KEY_DISPLAY_NAME, "").trim();
    }

    public void setDisplayName(@NonNull String name) {
        preferences.edit().putString(KEY_DISPLAY_NAME, name.trim()).apply();
    }

    // Dropdown helpers

    public int thresholdIndex() {
        int saved = getExpiryThresholdDays();
        for (int i = 0; i < THRESHOLD_OPTIONS.length; i++) {
            if (THRESHOLD_OPTIONS[i] == saved) {
                return i;
            }
        }
        return 0;
    }

    public static int thresholdForIndex(int index) {
        if (index < 0 || index >= THRESHOLD_OPTIONS.length) {
            return DEFAULT_THRESHOLD_DAYS;
        }
        return THRESHOLD_OPTIONS[index];
    }
}

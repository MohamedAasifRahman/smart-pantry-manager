package com.smartpantry.manager.activity;

import android.content.Intent;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smartpantry.manager.R;

// Wires up the bottom navigation once, so the tab screens do not each repeat it.
public abstract class BaseNavigationActivity extends AppCompatActivity {

    // Called by each tab screen with its own menu id, so the right tab is highlighted.
    protected void setUpBottomNavigation(@IdRes int selectedItemId) {
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        if (navigation == null) {
            return;
        }

        navigation.setSelectedItemId(selectedItemId);

        navigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == selectedItemId) {
                return true;
            }
            if (itemId == R.id.nav_pantry) {
                openTab(PantryActivity.class);
                return true;
            }
            if (itemId == R.id.nav_recipes) {
                openTab(SuggestedRecipesActivity.class);
                return true;
            }
            if (itemId == R.id.nav_settings) {
                openTab(SettingsActivity.class);
                return true;
            }
            return false;
        });
    }

    // CLEAR_TOP and SINGLE_TOP together stop the back stack growing every time a tab is tapped, so Back behaves predictably.
    private void openTab(Class<?> target) {
        Intent intent = new Intent(this, target);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

package com.smartpantry.manager.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.smartpantry.manager.R;

/**
 * The pantry screen and the launcher Activity of Smart Pantry Manager.
 *
 * <p>At this stage the screen only establishes the app bar and the Fresh Pantry
 * theme. The ingredient list, the add/edit flow and the bottom navigation are
 * added in later development stages, once the database layer exists.</p>
 */
public class PantryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        // The theme has no action bar of its own, so the Material toolbar in
        // the layout becomes this Activity's app bar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}

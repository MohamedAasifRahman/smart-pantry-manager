package com.smartpantry.manager.util;

// Names of every Intent extra used in the app
public final class IntentKeys {

    private IntentKeys() {
    }

    // The pantry ingredient to edit. Absent means add a new one.
    public static final String EXTRA_INGREDIENT_ID = "com.smartpantry.manager.INGREDIENT_ID";

    // The recipe the detail screen should display.
    public static final String EXTRA_RECIPE_ID = "com.smartpantry.manager.RECIPE_ID";

    // String resource id of the confirmation message to show after a save.
    public static final String EXTRA_RESULT_MESSAGE = "com.smartpantry.manager.RESULT_MESSAGE";
}

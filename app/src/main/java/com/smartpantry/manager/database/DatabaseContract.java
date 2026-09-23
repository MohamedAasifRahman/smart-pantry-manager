package com.smartpantry.manager.database;

// Every table and column name in one place, so nothing else types them as raw strings.
public final class DatabaseContract {

    private DatabaseContract() {
    }

    // Ingredients the user currently has at home.
    public static final class Pantry {
        public static final String TABLE_NAME = "pantry";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_UNIT = "unit";
        public static final String COLUMN_EXPIRY_DATE = "expiry_date";

        private Pantry() {
        }
    }

    // The recipe collection that ships with the app.
    public static final class Recipes {
        public static final String TABLE_NAME = "recipes";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_PREP_MINUTES = "prep_minutes";
        public static final String COLUMN_STEPS = "steps";

        private Recipes() {
        }
    }

    // The ingredients each recipe requires. Child rows of Recipes.
    public static final class RecipeIngredients {
        public static final String TABLE_NAME = "recipe_ingredients";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_UNIT = "unit";

        private RecipeIngredients() {
        }
    }
}

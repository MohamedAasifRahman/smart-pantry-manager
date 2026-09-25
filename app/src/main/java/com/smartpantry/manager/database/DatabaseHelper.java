package com.smartpantry.manager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smartpantry.manager.model.Ingredient;
import com.smartpantry.manager.model.Recipe;
import com.smartpantry.manager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Creates the SQLite database and performs every read and write the app needs.

// Writes go through ContentValues and reads use ? parameters, so user input is never concatenated into SQL.
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 2;

    private static final String SQL_CREATE_PANTRY =
            "CREATE TABLE " + DatabaseContract.Pantry.TABLE_NAME + " ("
                    + DatabaseContract.Pantry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DatabaseContract.Pantry.COLUMN_NAME + " TEXT NOT NULL, "
                    + DatabaseContract.Pantry.COLUMN_QUANTITY + " REAL NOT NULL, "
                    + DatabaseContract.Pantry.COLUMN_UNIT + " TEXT NOT NULL, "
                    + DatabaseContract.Pantry.COLUMN_EXPIRY_DATE + " TEXT)";

    private static final String SQL_CREATE_RECIPES =
            "CREATE TABLE " + DatabaseContract.Recipes.TABLE_NAME + " ("
                    + DatabaseContract.Recipes.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DatabaseContract.Recipes.COLUMN_NAME + " TEXT NOT NULL, "
                    + DatabaseContract.Recipes.COLUMN_CATEGORY + " TEXT NOT NULL, "
                    + DatabaseContract.Recipes.COLUMN_PREP_MINUTES + " INTEGER NOT NULL, "
                    + DatabaseContract.Recipes.COLUMN_STEPS + " TEXT NOT NULL)";

    private static final String SQL_CREATE_RECIPE_INGREDIENTS =
            "CREATE TABLE " + DatabaseContract.RecipeIngredients.TABLE_NAME + " ("
                    + DatabaseContract.RecipeIngredients.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DatabaseContract.RecipeIngredients.COLUMN_RECIPE_ID + " INTEGER NOT NULL, "
                    + DatabaseContract.RecipeIngredients.COLUMN_NAME + " TEXT NOT NULL, "
                    + DatabaseContract.RecipeIngredients.COLUMN_QUANTITY + " REAL NOT NULL, "
                    + DatabaseContract.RecipeIngredients.COLUMN_UNIT + " TEXT NOT NULL, "
                    + "FOREIGN KEY (" + DatabaseContract.RecipeIngredients.COLUMN_RECIPE_ID + ") "
                    + "REFERENCES " + DatabaseContract.Recipes.TABLE_NAME
                    + "(" + DatabaseContract.Recipes.COLUMN_ID + ") ON DELETE CASCADE)";

    private static final String SQL_CREATE_RECIPE_INGREDIENTS_INDEX =
            "CREATE INDEX idx_recipe_ingredients_recipe_id ON "
                    + DatabaseContract.RecipeIngredients.TABLE_NAME
                    + "(" + DatabaseContract.RecipeIngredients.COLUMN_RECIPE_ID + ")";

    public DatabaseHelper(Context context) {
        // Application context, so a finishing Activity cannot leak through this helper.
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    // SQLite ignores foreign keys unless they are switched on for the connection

    // so ON DELETE CASCADE would silently do nothing without this.
    @Override
    public void onConfigure(@NonNull SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Runs once per installation, when the database file is first created.
    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PANTRY);
        db.execSQL(SQL_CREATE_RECIPES);
        db.execSQL(SQL_CREATE_RECIPE_INGREDIENTS);
        db.execSQL(SQL_CREATE_RECIPE_INGREDIENTS_INDEX);
        seedRecipes(db);
    }

    // Writes the 20 bundled recipes and their required ingredients.

    private void seedRecipes(@NonNull SQLiteDatabase db) {
        List<Recipe> recipes = RecipeSeedData.buildSeedRecipes();
        db.beginTransaction();
        try {
            for (Recipe recipe : recipes) {
                ContentValues recipeValues = new ContentValues();
                recipeValues.put(DatabaseContract.Recipes.COLUMN_NAME, recipe.getName());
                recipeValues.put(DatabaseContract.Recipes.COLUMN_CATEGORY, recipe.getCategory());
                recipeValues.put(DatabaseContract.Recipes.COLUMN_PREP_MINUTES, recipe.getPrepMinutes());
                recipeValues.put(DatabaseContract.Recipes.COLUMN_STEPS, recipe.getSteps());

                long recipeId = db.insert(DatabaseContract.Recipes.TABLE_NAME, null, recipeValues);
                if (recipeId == -1) {
                    Log.e(TAG, "Could not seed recipe " + recipe.getName());
                    continue;
                }

                for (RecipeIngredient required : recipe.getIngredients()) {
                    ContentValues ingredientValues = new ContentValues();
                    ingredientValues.put(DatabaseContract.RecipeIngredients.COLUMN_RECIPE_ID, recipeId);
                    ingredientValues.put(DatabaseContract.RecipeIngredients.COLUMN_NAME, required.getName());
                    ingredientValues.put(DatabaseContract.RecipeIngredients.COLUMN_QUANTITY, required.getQuantity());
                    ingredientValues.put(DatabaseContract.RecipeIngredients.COLUMN_UNIT, required.getUnit());
                    db.insert(DatabaseContract.RecipeIngredients.TABLE_NAME, null, ingredientValues);
                }
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e(TAG, "Seeding the recipe collection failed", e);
        } finally {
            db.endTransaction();
        }
    }

    // The schema stays at version 1 for this submission, so no migration is written yet.
    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading from version " + oldVersion + " to " + newVersion + " destroys all data.");
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.RecipeIngredients.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Recipes.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Pantry.TABLE_NAME);
        onCreate(db);
    }

    // CREATE - returns the new row id, or Ingredient.NO_ID if the insert failed.
    public long insertIngredient(@NonNull Ingredient ingredient) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            long newId = db.insert(DatabaseContract.Pantry.TABLE_NAME, null, toContentValues(ingredient));
            if (newId == -1) {
                Log.e(TAG, "Insert returned -1 for " + ingredient.getName());
                return Ingredient.NO_ID;
            }
            return newId;
        } catch (SQLiteException e) {
            Log.e(TAG, "Could not insert ingredient", e);
            return Ingredient.NO_ID;
        }
    }

    // READ - every pantry ingredient, ordered by name and ignoring case.
    @NonNull
    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(
                    DatabaseContract.Pantry.TABLE_NAME,
                    null,
                    null, null, null, null,
                    DatabaseContract.Pantry.COLUMN_NAME + " COLLATE NOCASE ASC");
            while (cursor.moveToNext()) {
                ingredients.add(readIngredient(cursor));
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Could not read pantry ingredients", e);
        } finally {
            closeQuietly(cursor);
        }
        return ingredients;
    }

    // READ - one ingredient, or null when the id is unknown.
    @Nullable
    public Ingredient getIngredientById(long id) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(
                    DatabaseContract.Pantry.TABLE_NAME,
                    null,
                    DatabaseContract.Pantry.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null);
            if (cursor.moveToFirst()) {
                return readIngredient(cursor);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Could not read ingredient " + id, e);
        } finally {
            closeQuietly(cursor);
        }
        return null;
    }

    // UPDATE - true when exactly the expected row was changed.
    public boolean updateIngredient(@NonNull Ingredient ingredient) {
        if (ingredient.isNew()) {
            Log.e(TAG, "Refusing to update an ingredient that has no id");
            return false;
        }
        try {
            SQLiteDatabase db = getWritableDatabase();
            int rowsUpdated = db.update(
                    DatabaseContract.Pantry.TABLE_NAME,
                    toContentValues(ingredient),
                    DatabaseContract.Pantry.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(ingredient.getId())});
            return rowsUpdated == 1;
        } catch (SQLiteException e) {
            Log.e(TAG, "Could not update ingredient " + ingredient.getId(), e);
            return false;
        }
    }

    // DELETE - true when exactly one row was removed.
    public boolean deleteIngredient(long id) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            int rowsDeleted = db.delete(
                    DatabaseContract.Pantry.TABLE_NAME,
                    DatabaseContract.Pantry.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)});
            return rowsDeleted == 1;
        } catch (SQLiteException e) {
            Log.e(TAG, "Could not delete ingredient " + id, e);
            return false;
        }
    }

    // Loads every recipe with the ingredients it requires

    @NonNull
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        Map<Long, List<RecipeIngredient>> ingredientsByRecipe = getAllRecipeIngredients();

        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(
                    DatabaseContract.Recipes.TABLE_NAME,
                    null, null, null, null, null,
                    DatabaseContract.Recipes.COLUMN_NAME + " COLLATE NOCASE ASC");
            while (cursor.moveToNext()) {
                Recipe recipe = readRecipe(cursor);
                List<RecipeIngredient> required = ingredientsByRecipe.get(recipe.getId());
                if (required != null) {
                    for (RecipeIngredient ingredient : required) {
                        recipe.addIngredient(ingredient);
                    }
                }
                recipes.add(recipe);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Could not read recipes", e);
        } finally {
            closeQuietly(cursor);
        }
        return recipes;
    }

    // One recipe with its ingredients, or null when the id is unknown.
    @Nullable
    public Recipe getRecipeById(long id) {
        Recipe recipe = null;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(
                    DatabaseContract.Recipes.TABLE_NAME,
                    null,
                    DatabaseContract.Recipes.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null);
            if (cursor.moveToFirst()) {
                recipe = readRecipe(cursor);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Could not read recipe " + id, e);
        } finally {
            closeQuietly(cursor);
        }

        if (recipe == null) {
            return null;
        }

        Cursor ingredientCursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            ingredientCursor = db.query(
                    DatabaseContract.RecipeIngredients.TABLE_NAME,
                    null,
                    DatabaseContract.RecipeIngredients.COLUMN_RECIPE_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null, null,
                    DatabaseContract.RecipeIngredients.COLUMN_ID + " ASC");
            while (ingredientCursor.moveToNext()) {
                recipe.addIngredient(readRecipeIngredient(ingredientCursor));
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Could not read ingredients for recipe " + id, e);
        } finally {
            closeQuietly(ingredientCursor);
        }
        return recipe;
    }

    // Every recipe ingredient, grouped by the recipe it belongs to.
    @NonNull
    private Map<Long, List<RecipeIngredient>> getAllRecipeIngredients() {
        Map<Long, List<RecipeIngredient>> grouped = new HashMap<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(
                    DatabaseContract.RecipeIngredients.TABLE_NAME,
                    null, null, null, null, null,
                    DatabaseContract.RecipeIngredients.COLUMN_ID + " ASC");
            while (cursor.moveToNext()) {
                RecipeIngredient ingredient = readRecipeIngredient(cursor);
                List<RecipeIngredient> forRecipe = grouped.get(ingredient.getRecipeId());
                if (forRecipe == null) {
                    forRecipe = new ArrayList<>();
                    grouped.put(ingredient.getRecipeId(), forRecipe);
                }
                forRecipe.add(ingredient);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Could not read recipe ingredients", e);
        } finally {
            closeQuietly(cursor);
        }
        return grouped;
    }

    // Model object to column values.
    private ContentValues toContentValues(@NonNull Ingredient ingredient) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Pantry.COLUMN_NAME, ingredient.getName());
        values.put(DatabaseContract.Pantry.COLUMN_QUANTITY, ingredient.getQuantity());
        values.put(DatabaseContract.Pantry.COLUMN_UNIT, ingredient.getUnit());
        if (ingredient.hasExpiryDate()) {
            values.put(DatabaseContract.Pantry.COLUMN_EXPIRY_DATE, ingredient.getExpiryDate());
        } else {
            values.putNull(DatabaseContract.Pantry.COLUMN_EXPIRY_DATE);
        }
        return values;
    }

    // Current cursor row to a model object.
    private Ingredient readIngredient(@NonNull Cursor cursor) {
        int expiryIndex = cursor.getColumnIndexOrThrow(DatabaseContract.Pantry.COLUMN_EXPIRY_DATE);
        return new Ingredient(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Pantry.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Pantry.COLUMN_NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.Pantry.COLUMN_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Pantry.COLUMN_UNIT)),
                cursor.isNull(expiryIndex) ? null : cursor.getString(expiryIndex));
    }

    // Current cursor row to a recipe, without its ingredients.
    private Recipe readRecipe(@NonNull Cursor cursor) {
        return new Recipe(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Recipes.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Recipes.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Recipes.COLUMN_CATEGORY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.Recipes.COLUMN_PREP_MINUTES)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Recipes.COLUMN_STEPS)));
    }

    // Current cursor row to a required recipe ingredient.
    private RecipeIngredient readRecipeIngredient(@NonNull Cursor cursor) {
        return new RecipeIngredient(
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.RecipeIngredients.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.RecipeIngredients.COLUMN_RECIPE_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.RecipeIngredients.COLUMN_NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.RecipeIngredients.COLUMN_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.RecipeIngredients.COLUMN_UNIT)));
    }

    private void closeQuietly(@Nullable Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }
}

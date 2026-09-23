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

import java.util.ArrayList;
import java.util.List;

// Creates the SQLite database and performs every read and write the app needs.

// Writes go through ContentValues and reads use ? parameters, so user input is never concatenated into SQL.
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

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

    private void closeQuietly(@Nullable Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }
}

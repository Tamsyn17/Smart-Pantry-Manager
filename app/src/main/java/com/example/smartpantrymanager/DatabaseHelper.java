package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_PANTRY = "pantry_items";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_EXPIRY_DATE = "expiry_date";

    public static final String COLUMN_RECIPE_ID = "recipe_id";
    public static final String COLUMN_INSTRUCTIONS = "instructions";
    public static final String COLUMN_INGREDIENT_NAME = "ingredient_name";

    private static final String CREATE_PANTRY_TABLE =
            "CREATE TABLE " + TABLE_PANTRY + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_QUANTITY + " REAL NOT NULL, " +
                    COLUMN_UNIT + " TEXT NOT NULL, " +
                    COLUMN_EXPIRY_DATE + " TEXT" +
                    ")";

    private static final String CREATE_RECIPES_TABLE =
            "CREATE TABLE " + TABLE_RECIPES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_INSTRUCTIONS + " TEXT NOT NULL" +
                    ")";

    private static final String CREATE_RECIPE_INGREDIENTS_TABLE =
            "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +
                    COLUMN_INGREDIENT_NAME + " TEXT NOT NULL, " +
                    COLUMN_QUANTITY + " REAL NOT NULL, " +
                    COLUMN_UNIT + " TEXT NOT NULL, " +
                    "FOREIGN KEY(" + COLUMN_RECIPE_ID + ") REFERENCES " +
                    TABLE_RECIPES + "(" + COLUMN_ID + ") ON DELETE CASCADE" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_PANTRY_TABLE);
        db.execSQL(CREATE_RECIPES_TABLE);
        db.execSQL(CREATE_RECIPE_INGREDIENTS_TABLE);

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {

            db.execSQL(CREATE_RECIPES_TABLE);
            db.execSQL(CREATE_RECIPE_INGREDIENTS_TABLE);

            seedRecipes(db);
        }
    }

    // -------------------------
    // PANTRY CRUD
    // -------------------------

    public long addPantryItem(PantryItem item) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_UNIT, item.getUnit());
        values.put(COLUMN_EXPIRY_DATE, item.getExpiryDate());

        long result = db.insert(TABLE_PANTRY, null, values);

        db.close();

        return result;
    }

    public List<PantryItem> getAllPantryItems() {

        List<PantryItem> pantryItems = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                COLUMN_NAME + " ASC"
        );

        if (cursor.moveToFirst()) {

            do {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(COLUMN_ID));

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_NAME));

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_UNIT));

                String expiryDate = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_EXPIRY_DATE));

                PantryItem item = new PantryItem(
                        id,
                        name,
                        quantity,
                        unit,
                        expiryDate
                );

                pantryItems.add(item);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return pantryItems;
    }

    public int updatePantryItem(PantryItem item) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_QUANTITY, item.getQuantity());
        values.put(COLUMN_UNIT, item.getUnit());
        values.put(COLUMN_EXPIRY_DATE, item.getExpiryDate());

        int rowsUpdated = db.update(
                TABLE_PANTRY,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(item.getId())}
        );

        db.close();

        return rowsUpdated;
    }

    public int deletePantryItem(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int rowsDeleted = db.delete(
                TABLE_PANTRY,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return rowsDeleted;
    }

    // -------------------------
    // RECIPE READ METHODS
    // -------------------------

    public List<Recipe> getAllRecipes() {

        List<Recipe> recipes = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                COLUMN_NAME + " ASC"
        );

        if (cursor.moveToFirst()) {

            do {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(COLUMN_ID));

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_NAME));

                String instructions = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTIONS));

                Recipe recipe = new Recipe(
                        id,
                        name,
                        instructions
                );

                recipes.add(recipe);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return recipes;
    }

    public List<RecipeIngredient> getIngredientsForRecipe(int recipeId) {

        List<RecipeIngredient> ingredients = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                COLUMN_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                COLUMN_INGREDIENT_NAME + " ASC"
        );

        if (cursor.moveToFirst()) {

            do {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(COLUMN_ID));

                String ingredientName = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_NAME));

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_UNIT));

                RecipeIngredient ingredient =
                        new RecipeIngredient(
                                id,
                                recipeId,
                                ingredientName,
                                quantity,
                                unit
                        );

                ingredients.add(ingredient);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return ingredients;
    }

    // -------------------------
    // RECIPE SEEDING
    // -------------------------

    private void seedRecipes(SQLiteDatabase db) {

        addRecipeWithIngredients(
                db,
                "Scrambled Eggs",
                "Beat the eggs. Heat butter in a pan. Add the eggs and cook while stirring until soft and fully cooked.",
                new String[]{"Egg", "Butter"},
                new double[]{2, 10},
                new String[]{"pieces", "g"}
        );

        addRecipeWithIngredients(
                db,
                "Cheese Omelette",
                "Beat the eggs. Pour into a heated pan. Add cheese and cook until the omelette is set.",
                new String[]{"Egg", "Cheese", "Butter"},
                new double[]{2, 50, 10},
                new String[]{"pieces", "g", "g"}
        );

        addRecipeWithIngredients(
                db,
                "Boiled Rice",
                "Rinse the rice. Add rice and water to a pot. Cook until the water is absorbed and the rice is tender.",
                new String[]{"Rice", "Water"},
                new double[]{1, 2},
                new String[]{"cup", "cups"}
        );

        addRecipeWithIngredients(
                db,
                "Toast",
                "Place the bread in a toaster or pan and toast until golden.",
                new String[]{"Bread"},
                new double[]{2},
                new String[]{"slices"}
        );

        addRecipeWithIngredients(
                db,
                "Butter Toast",
                "Toast the bread until golden, then spread butter over each slice.",
                new String[]{"Bread", "Butter"},
                new double[]{2, 10},
                new String[]{"slices", "g"}
        );

        addRecipeWithIngredients(
                db,
                "Cheese Toast",
                "Place cheese on the bread and toast until the cheese has melted.",
                new String[]{"Bread", "Cheese"},
                new double[]{2, 50},
                new String[]{"slices", "g"}
        );

        addRecipeWithIngredients(
                db,
                "Tomato Sandwich",
                "Slice the tomato and place it between two slices of bread.",
                new String[]{"Bread", "Tomato"},
                new double[]{2, 1},
                new String[]{"slices", "pieces"}
        );

        addRecipeWithIngredients(
                db,
                "Cheese Sandwich",
                "Place cheese between two slices of bread and serve.",
                new String[]{"Bread", "Cheese"},
                new double[]{2, 50},
                new String[]{"slices", "g"}
        );

        addRecipeWithIngredients(
                db,
                "Egg Sandwich",
                "Cook the egg, place it between two slices of bread, and serve.",
                new String[]{"Bread", "Egg"},
                new double[]{2, 1},
                new String[]{"slices", "pieces"}
        );

        addRecipeWithIngredients(
                db,
                "Tomato Pasta",
                "Cook the pasta. Heat tomato sauce in a pan and combine with the cooked pasta.",
                new String[]{"Pasta", "Tomato Sauce"},
                new double[]{200, 150},
                new String[]{"g", "ml"}
        );

        addRecipeWithIngredients(
                db,
                "Cheesy Pasta",
                "Cook the pasta until tender. Drain and mix with grated cheese.",
                new String[]{"Pasta", "Cheese"},
                new double[]{200, 75},
                new String[]{"g", "g"}
        );

        addRecipeWithIngredients(
                db,
                "Chicken Rice",
                "Cook the rice. Cook the chicken separately, then combine and serve.",
                new String[]{"Chicken", "Rice"},
                new double[]{200, 1},
                new String[]{"g", "cup"}
        );

        addRecipeWithIngredients(
                db,
                "Chicken Pasta",
                "Cook the pasta. Cook the chicken, slice it, and combine with the pasta.",
                new String[]{"Chicken", "Pasta"},
                new double[]{200, 200},
                new String[]{"g", "g"}
        );

        addRecipeWithIngredients(
                db,
                "Mashed Potatoes",
                "Boil the potatoes until soft. Mash with milk and butter until smooth.",
                new String[]{"Potato", "Milk", "Butter"},
                new double[]{3, 100, 20},
                new String[]{"pieces", "ml", "g"}
        );

        addRecipeWithIngredients(
                db,
                "Fried Potatoes",
                "Slice the potatoes and fry them in oil until golden and cooked through.",
                new String[]{"Potato", "Oil"},
                new double[]{3, 30},
                new String[]{"pieces", "ml"}
        );

        addRecipeWithIngredients(
                db,
                "Simple Pancakes",
                "Mix flour, milk and egg into a smooth batter. Cook small portions in a heated pan.",
                new String[]{"Flour", "Milk", "Egg"},
                new double[]{1, 250, 1},
                new String[]{"cup", "ml", "pieces"}
        );

        addRecipeWithIngredients(
                db,
                "Banana Pancakes",
                "Mash the banana. Mix with egg and flour. Cook portions of the mixture in a heated pan.",
                new String[]{"Banana", "Egg", "Flour"},
                new double[]{1, 1, 100},
                new String[]{"pieces", "pieces", "g"}
        );

        addRecipeWithIngredients(
                db,
                "Fruit Bowl",
                "Wash and slice the fruit. Combine everything in a bowl and serve.",
                new String[]{"Apple", "Banana", "Orange"},
                new double[]{1, 1, 1},
                new String[]{"pieces", "pieces", "pieces"}
        );

        addRecipeWithIngredients(
                db,
                "Milk and Cereal",
                "Place cereal in a bowl and pour milk over it.",
                new String[]{"Cereal", "Milk"},
                new double[]{1, 200},
                new String[]{"cup", "ml"}
        );

        addRecipeWithIngredients(
                db,
                "Peanut Butter Toast",
                "Toast the bread and spread peanut butter evenly over each slice.",
                new String[]{"Bread", "Peanut Butter"},
                new double[]{2, 30},
                new String[]{"slices", "g"}
        );
    }

    private void addRecipeWithIngredients(
            SQLiteDatabase db,
            String recipeName,
            String instructions,
            String[] ingredientNames,
            double[] quantities,
            String[] units) {

        ContentValues recipeValues = new ContentValues();

        recipeValues.put(COLUMN_NAME, recipeName);
        recipeValues.put(COLUMN_INSTRUCTIONS, instructions);

        long recipeId = db.insert(
                TABLE_RECIPES,
                null,
                recipeValues
        );

        if (recipeId == -1) {
            return;
        }

        for (int i = 0; i < ingredientNames.length; i++) {

            ContentValues ingredientValues = new ContentValues();

            ingredientValues.put(COLUMN_RECIPE_ID, recipeId);
            ingredientValues.put(
                    COLUMN_INGREDIENT_NAME,
                    ingredientNames[i]
            );
            ingredientValues.put(
                    COLUMN_QUANTITY,
                    quantities[i]
            );
            ingredientValues.put(
                    COLUMN_UNIT,
                    units[i]
            );

            db.insert(
                    TABLE_RECIPE_INGREDIENTS,
                    null,
                    ingredientValues
            );
        }
    }
}
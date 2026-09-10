package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRecipes;
    private RecipeAdapter recipeAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        recyclerViewRecipes = findViewById(R.id.recyclerViewRecipes);

        databaseHelper = new DatabaseHelper(this);

        recyclerViewRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {

        List<Recipe> allRecipes =
                databaseHelper.getAllRecipes();

        List<PantryItem> pantryItems =
                databaseHelper.getAllPantryItems();

        List<Recipe> suggestedRecipes =
                new ArrayList<>();

        for (Recipe recipe : allRecipes) {

            if (canMakeRecipe(recipe, pantryItems)) {
                suggestedRecipes.add(recipe);
            }
        }

        recipeAdapter =
                new RecipeAdapter(suggestedRecipes);

        recyclerViewRecipes.setAdapter(recipeAdapter);

        if (suggestedRecipes.isEmpty()) {

            Toast.makeText(
                    this,
                    "No recipes can be made with your current pantry",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private boolean canMakeRecipe(
            Recipe recipe,
            List<PantryItem> pantryItems) {

        List<RecipeIngredient> requiredIngredients =
                databaseHelper.getIngredientsForRecipe(
                        recipe.getId()
                );

        for (RecipeIngredient required : requiredIngredients) {

            if (!hasRequiredIngredient(required, pantryItems)) {
                return false;
            }
        }

        return true;
    }

    private boolean hasRequiredIngredient(
            RecipeIngredient required,
            List<PantryItem> pantryItems) {

        String requiredUnit =
                normalizeUnit(required.getUnit());

        String requiredUnitType =
                getUnitType(requiredUnit);

        double requiredQuantity =
                convertToBaseUnit(
                        required.getQuantity(),
                        requiredUnit
                );

        double totalAvailableQuantity = 0;

        for (PantryItem pantryItem : pantryItems) {

            if (!ingredientNamesMatch(
                    required.getIngredientName(),
                    pantryItem.getName())) {

                continue;
            }

            String pantryUnit =
                    normalizeUnit(pantryItem.getUnit());

            String pantryUnitType =
                    getUnitType(pantryUnit);

            if (!requiredUnitType.equals(pantryUnitType)) {
                continue;
            }

            double pantryQuantity =
                    convertToBaseUnit(
                            pantryItem.getQuantity(),
                            pantryUnit
                    );

            totalAvailableQuantity += pantryQuantity;
        }

        return totalAvailableQuantity >= requiredQuantity;
    }

    private boolean ingredientNamesMatch(
            String recipeIngredient,
            String pantryIngredient) {

        String recipeName =
                normalizeIngredientName(recipeIngredient);

        String pantryName =
                normalizeIngredientName(pantryIngredient);

        return recipeName.equals(pantryName);
    }

    private String normalizeIngredientName(String name) {

        if (name == null) {
            return "";
        }

        String normalized =
                name.trim()
                        .toLowerCase(Locale.ROOT)
                        .replace("-", " ")
                        .replaceAll("\\s+", " ");

        // Common irregular/simple plural forms
        if (normalized.equals("tomatoes")) {
            return "tomato";
        }

        if (normalized.equals("potatoes")) {
            return "potato";
        }

        if (normalized.endsWith("ies")
                && normalized.length() > 3) {

            return normalized.substring(
                    0,
                    normalized.length() - 3
            ) + "y";
        }

        if (normalized.endsWith("es")
                && normalized.length() > 2) {

            if (normalized.endsWith("ches")
                    || normalized.endsWith("shes")
                    || normalized.endsWith("xes")) {

                return normalized.substring(
                        0,
                        normalized.length() - 2
                );
            }
        }

        if (normalized.endsWith("s")
                && !normalized.endsWith("ss")
                && normalized.length() > 1) {

            return normalized.substring(
                    0,
                    normalized.length() - 1
            );
        }

        return normalized;
    }

    private String normalizeUnit(String unit) {

        if (unit == null) {
            return "";
        }

        String normalized =
                unit.trim().toLowerCase(Locale.ROOT);

        switch (normalized) {

            case "g":
            case "gram":
            case "grams":
                return "g";

            case "kg":
            case "kgs":
            case "kilogram":
            case "kilograms":
                return "kg";

            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "ml";

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "l";

            case "piece":
            case "pieces":
            case "pc":
            case "pcs":
                return "piece";

            case "slice":
            case "slices":
                return "slice";

            case "cup":
            case "cups":
                return "cup";

            default:
                return normalized;
        }
    }

    private double convertToBaseUnit(
            double quantity,
            String unit) {

        switch (unit) {

            case "kg":
                return quantity * 1000;

            case "l":
                return quantity * 1000;

            default:
                return quantity;
        }
    }

    private String getUnitType(String unit) {

        switch (unit) {

            case "g":
            case "kg":
                return "mass";

            case "ml":
            case "l":
                return "volume";

            case "piece":
                return "piece";

            case "slice":
                return "slice";

            case "cup":
                return "cup";

            default:
                return unit;
        }
    }
}
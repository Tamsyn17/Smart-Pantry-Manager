package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

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

            boolean ingredientFound = false;

            for (PantryItem pantryItem : pantryItems) {

                if (ingredientNamesMatch(
                        required.getIngredientName(),
                        pantryItem.getName())) {

                    if (hasEnoughQuantity(
                            pantryItem,
                            required)) {

                        ingredientFound = true;
                        break;
                    }
                }
            }

            if (!ingredientFound) {
                return false;
            }
        }

        return true;
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
                name.trim().toLowerCase();

        if (normalized.endsWith("ies")
                && normalized.length() > 3) {

            normalized =
                    normalized.substring(
                            0,
                            normalized.length() - 3
                    ) + "y";

        } else if (normalized.endsWith("es")
                && normalized.length() > 2) {

            if (normalized.endsWith("oes")) {

                normalized =
                        normalized.substring(
                                0,
                                normalized.length() - 2
                        );

            } else if (normalized.endsWith("ches")
                    || normalized.endsWith("shes")
                    || normalized.endsWith("xes")) {

                normalized =
                        normalized.substring(
                                0,
                                normalized.length() - 2
                        );
            }

        } else if (normalized.endsWith("s")
                && normalized.length() > 1) {

            normalized =
                    normalized.substring(
                            0,
                            normalized.length() - 1
                    );
        }

        return normalized;
    }

    private boolean hasEnoughQuantity(
            PantryItem pantryItem,
            RecipeIngredient required) {

        String pantryUnit =
                normalizeUnit(pantryItem.getUnit());

        String recipeUnit =
                normalizeUnit(required.getUnit());

        double pantryQuantity =
                convertToBaseUnit(
                        pantryItem.getQuantity(),
                        pantryUnit
                );

        double requiredQuantity =
                convertToBaseUnit(
                        required.getQuantity(),
                        recipeUnit
                );

        String pantryUnitType =
                getUnitType(pantryUnit);

        String recipeUnitType =
                getUnitType(recipeUnit);

        if (!pantryUnitType.equals(recipeUnitType)) {
            return false;
        }

        return pantryQuantity >= requiredQuantity;
    }

    private String normalizeUnit(String unit) {

        if (unit == null) {
            return "";
        }

        String normalized =
                unit.trim().toLowerCase();

        switch (normalized) {

            case "gram":
            case "grams":
            case "g":
                return "g";

            case "kilogram":
            case "kilograms":
            case "kg":
                return "kg";

            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
            case "ml":
                return "ml";

            case "litre":
            case "litres":
            case "liter":
            case "liters":
            case "l":
                return "l";

            case "piece":
            case "pieces":
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
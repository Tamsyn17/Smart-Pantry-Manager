package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvRecipeDetailName;
    private TextView tvRecipeIngredients;
    private TextView tvRecipeDetailInstructions;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        tvRecipeDetailName = findViewById(R.id.tvRecipeDetailName);
        tvRecipeIngredients = findViewById(R.id.tvRecipeIngredients);
        tvRecipeDetailInstructions = findViewById(R.id.tvRecipeDetailInstructions);

        databaseHelper = new DatabaseHelper(this);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);
        String recipeName = getIntent().getStringExtra("recipe_name");
        String recipeInstructions = getIntent().getStringExtra("recipe_instructions");

        tvRecipeDetailName.setText(recipeName);
        tvRecipeDetailInstructions.setText(recipeInstructions);

        loadRecipeIngredients(recipeId);
    }

    private void loadRecipeIngredients(int recipeId) {

        List<RecipeIngredient> ingredients =
                databaseHelper.getIngredientsForRecipe(recipeId);

        StringBuilder ingredientText = new StringBuilder();

        for (RecipeIngredient ingredient : ingredients) {

            ingredientText.append("• ")
                    .append(ingredient.getIngredientName())
                    .append(" - ")
                    .append(ingredient.getQuantity())
                    .append(" ")
                    .append(ingredient.getUnit())
                    .append("\n");
        }

        tvRecipeIngredients.setText(ingredientText.toString());
    }
}
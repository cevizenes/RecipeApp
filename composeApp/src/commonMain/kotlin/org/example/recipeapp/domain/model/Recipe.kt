package org.example.recipeapp.domain.model

import kotlin.math.round

data class Recipe(
    val id: Int,
    val title: String,
    val image: String?,
    val readyInMinutes: Int?,
    val servings: Int?,
    val summary: String?,
    val cuisines: List<String>,
    val dishTypes: List<String>,
    val diets: List<String>,
    val vegetarian: Boolean,
    val vegan: Boolean,
    val glutenFree: Boolean,
    val aggregateLikes: Int?,
    val spoonacularScore: Double?,
) {
    val displayTime: String
        get() = readyInMinutes?.let { "$it min" } ?: "N/A"

    val displayServings: String
        get() = servings?.let { "Serves $it" } ?: "N/A"

    val displayScore: String
        get() = spoonacularScore?.let { scoreValue ->
            val calculatedScore = scoreValue / 20.0
            if (calculatedScore.isFinite()) {
                val roundedToOneDecimalPlace = round(calculatedScore * 10) / 10.0

                val scoreString = roundedToOneDecimalPlace.toString()

                if (roundedToOneDecimalPlace == round(roundedToOneDecimalPlace) && !scoreString.contains('.')) {
                    "$scoreString.0★"
                } else {
                    "$scoreString★"
                }
            } else {
                "N/A"
            }
        } ?: "N/A"
}

data class RecipeDetail(
    val id: Int,
    val title: String,
    val image: String?,
    val readyInMinutes: Int?,
    val servings: Int?,
    val summary: String?,
    val cuisines: List<String>,
    val dishTypes: List<String>,
    val diets: List<String>,
    val instructions: String?,
    val ingredients: List<Ingredient>,
    val steps: List<RecipeStep>,
    val nutrition: Nutrition?,
    val vegetarian: Boolean,
    val vegan: Boolean,
    val glutenFree: Boolean,
    val aggregateLikes: Int?,
    val spoonacularScore: Double?,
)

data class Ingredient(
    val id: Int,
    val name: String,
    val original: String,
    val amount: Double,
    val unit: String,
    val image: String?,
)

data class RecipeStep(
    val number: Int,
    val instruction: String,
)

data class Nutrition(
    val calories: Double,
    val fat: Double,
    val protein: Double,
    val carbs: Double,
)
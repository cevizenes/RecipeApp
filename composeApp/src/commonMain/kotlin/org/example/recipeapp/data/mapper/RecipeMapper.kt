package org.example.recipeapp.data.mapper

import org.example.recipeapp.data.remote.dto.IngredientDto
import org.example.recipeapp.data.remote.dto.RecipeDetailDto
import org.example.recipeapp.data.remote.dto.RecipeDto
import org.example.recipeapp.domain.model.Ingredient
import org.example.recipeapp.domain.model.Nutrition
import org.example.recipeapp.domain.model.Recipe
import org.example.recipeapp.domain.model.RecipeDetail
import org.example.recipeapp.domain.model.RecipeStep

fun RecipeDto.toDomain(): Recipe = Recipe(
    id = id,
    title = title,
    image = image,
    readyInMinutes = readyInMinutes,
    servings = servings,
    summary = summary,
    cuisines = cuisines,
    dishTypes = dishTypes,
    diets = diets,
    vegetarian = vegetarian,
    vegan = vegan,
    glutenFree = glutenFree,
    aggregateLikes = aggregateLikes,
    spoonacularScore = spoonacularScore
)

fun RecipeDetailDto.toDomain(): RecipeDetail {
    val nutritionData = nutrition?.nutrients?.let { nutrients ->
        Nutrition(
            calories = nutrients.find { it.name == "Calories" }?.amount ?: 0.0,
            fat = nutrients.find { it.name == "Fat" }?.amount ?: 0.0,
            protein = nutrients.find { it.name == "Protein" }?.amount ?: 0.0,
            carbs = nutrients.find { it.name == "Carbohydrates" }?.amount ?: 0.0
        )
    }

    val recipeSteps = analyzedInstructions.firstOrNull()?.steps?.map { step ->
        RecipeStep(
            number = step.number,
            instruction = step.step
        )
    } ?: emptyList()

    return RecipeDetail(
        id = id,
        title = title,
        image = image,
        readyInMinutes = readyInMinutes,
        servings = servings,
        summary = summary,
        cuisines = cuisines,
        dishTypes = dishTypes,
        diets = diets,
        instructions = instructions,
        ingredients = extendedIngredients.map { it.toDomain() },
        steps = recipeSteps,
        nutrition = nutritionData,
        vegetarian = vegetarian,
        vegan = vegan,
        glutenFree = glutenFree,
        aggregateLikes = aggregateLikes,
        spoonacularScore = spoonacularScore
    )
}

fun IngredientDto.toDomain(): Ingredient = Ingredient(
    id = id,
    name = name,
    original = original,
    amount = amount,
    unit = unit,
    image = image
)
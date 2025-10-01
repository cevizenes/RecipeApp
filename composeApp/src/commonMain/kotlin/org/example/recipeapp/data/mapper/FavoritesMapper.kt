package org.example.recipeapp.data.mapper

import org.example.recipeapp.db.Favorites
import org.example.recipeapp.domain.model.FavoriteRecipe
import org.example.recipeapp.domain.model.Recipe
import org.example.recipeapp.domain.model.RecipeDetail

fun Favorites.asDomain() = FavoriteRecipe(
    id = id.toInt(),
    title = title,
    image = image,
    readyInMinutes = readyInMinutes?.toInt(),
    score = score
)

fun Recipe.asFavorite() = FavoriteRecipe(
    id = id,
    title = title,
    image = image,
    readyInMinutes = readyInMinutes,
    score = spoonacularScore
)

fun RecipeDetail.asFavorite() = FavoriteRecipe(
    id = id,
    title = title,
    image = image,
    readyInMinutes = readyInMinutes,
    score = spoonacularScore
)
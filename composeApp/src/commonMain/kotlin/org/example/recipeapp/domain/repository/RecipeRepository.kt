package org.example.recipeapp.domain.repository

import org.example.recipeapp.core.util.Result
import org.example.recipeapp.domain.model.Recipe
import org.example.recipeapp.domain.model.RecipeDetail

interface RecipeRepository {
    suspend fun searchRecipes(
        query: String,
        cuisine: String? = null,
        diet: String? = null,
        type: String? = null,
        maxReadyTime: Int? = null
    ): Result<List<Recipe>>

    suspend fun getRecipeDetail(id: Int): Result<RecipeDetail>
    suspend fun getRandomRecipes(count: Int = 10): Result<List<Recipe>>
}
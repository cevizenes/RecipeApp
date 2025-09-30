package org.example.recipeapp.data.repository

import org.example.recipeapp.core.util.Result
import org.example.recipeapp.data.mapper.toDomain
import org.example.recipeapp.data.remote.api.SpoonacularApi
import org.example.recipeapp.domain.model.Recipe
import org.example.recipeapp.domain.model.RecipeDetail
import org.example.recipeapp.domain.repository.RecipeRepository

class RecipeRepositoryImpl(
    private val api: SpoonacularApi
) : RecipeRepository {

    override suspend fun searchRecipes(
        query: String,
        cuisine: String?,
        diet: String?,
        type: String?,
        maxReadyTime: Int?
    ): Result<List<Recipe>> = try {
        val response = api.searchRecipes(
            query = query,
            cuisine = cuisine,
            diet = diet,
            type = type,
            maxReadyTime = maxReadyTime
        )
        Result.Success(response.results.map { it.toDomain() })
    } catch (e: Exception) {
        Result.Error(e, e.message)
    }

    override suspend fun getRecipeDetail(id: Int): Result<RecipeDetail> = try {
        val response = api.getRecipeDetail(id)
        Result.Success(response.toDomain())
    } catch (e: Exception) {
        Result.Error(e, e.message)
    }

    override suspend fun getRandomRecipes(count: Int): Result<List<Recipe>> = try {
        val response = api.getRandomRecipes(count)
        Result.Success(response.results.map { it.toDomain() })
    } catch (e: Exception) {
        Result.Error(e, e.message)
    }
}
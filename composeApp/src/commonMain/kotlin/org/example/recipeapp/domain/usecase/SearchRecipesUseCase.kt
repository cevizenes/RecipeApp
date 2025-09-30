package org.example.recipeapp.domain.usecase

import org.example.recipeapp.core.util.Result
import org.example.recipeapp.domain.model.Recipe
import org.example.recipeapp.domain.repository.RecipeRepository

class SearchRecipesUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(
        query: String,
        cuisine: String? = null,
        diet: String? = null,
        type: String? = null,
        maxReadyTime: Int? = null
    ): Result<List<Recipe>> {
        if (query.isBlank()) {
            return Result.Error(IllegalArgumentException("Query cannot be empty"))
        }
        return repository.searchRecipes(query, cuisine, diet, type, maxReadyTime)
    }
}
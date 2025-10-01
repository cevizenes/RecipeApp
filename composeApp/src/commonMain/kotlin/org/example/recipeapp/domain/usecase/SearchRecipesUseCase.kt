package org.example.recipeapp.domain.usecase

import org.example.recipeapp.core.util.Result
import org.example.recipeapp.domain.model.Recipe
import org.example.recipeapp.domain.repository.RecipeRepository

class SearchRecipesUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(
        query: String?,                    // ✅ nullable
        cuisine: String? = null,
        diet: String? = null,
        type: String? = null,
        maxReadyTime: Int? = null
    ): Result<List<Recipe>> {
        if ((query == null || query.isBlank()) && type.isNullOrBlank()) {
            return Result.Error(IllegalArgumentException("Either query or type must be provided"))
        }
        return repository.searchRecipes(query ?: "", cuisine, diet, type, maxReadyTime)
    }
}
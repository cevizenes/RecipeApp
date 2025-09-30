package org.example.recipeapp.domain.usecase

import org.example.recipeapp.core.util.Result
import org.example.recipeapp.domain.model.RecipeDetail
import org.example.recipeapp.domain.repository.RecipeRepository

class GetRecipeDetailUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(id: Int): Result<RecipeDetail> {
        return repository.getRecipeDetail(id)
    }
}
package org.example.recipeapp.domain.usecase

import org.example.recipeapp.core.util.Result
import org.example.recipeapp.domain.model.Recipe
import org.example.recipeapp.domain.repository.RecipeRepository

class GetRandomRecipesUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(count: Int = 10): Result<List<Recipe>> {
        return repository.getRandomRecipes(count)
    }
}
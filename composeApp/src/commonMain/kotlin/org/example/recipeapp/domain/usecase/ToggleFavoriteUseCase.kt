package org.example.recipeapp.domain.usecase

import org.example.recipeapp.domain.model.FavoriteRecipe
import org.example.recipeapp.domain.repository.FavoritesRepository

class ToggleFavoriteUseCase(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(favorite: FavoriteRecipe): Boolean = favoritesRepository.toggle(favorite)
}
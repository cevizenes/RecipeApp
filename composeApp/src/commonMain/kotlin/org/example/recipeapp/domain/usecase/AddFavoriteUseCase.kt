package org.example.recipeapp.domain.usecase

import org.example.recipeapp.domain.model.FavoriteRecipe
import org.example.recipeapp.domain.repository.FavoritesRepository

class AddFavoriteUseCase(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(favorites: FavoriteRecipe) = favoritesRepository.add(favorites)
}
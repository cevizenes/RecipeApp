package org.example.recipeapp.domain.usecase

import org.example.recipeapp.domain.repository.FavoritesRepository

class IsFavoriteUseCase(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(id: Int): Boolean = favoritesRepository.isFavorite(id)
}
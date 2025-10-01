package org.example.recipeapp.domain.usecase

import org.example.recipeapp.domain.repository.FavoritesRepository

class RemoveFavoriteUseCase(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(id: Int) = favoritesRepository.remove(id)
}
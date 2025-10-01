package org.example.recipeapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.example.recipeapp.domain.model.FavoriteRecipe
import org.example.recipeapp.domain.repository.FavoritesRepository

class ObserveFavoritesUseCase(
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(): Flow<List<FavoriteRecipe>> = favoritesRepository.observeAll()
}
package org.example.recipeapp.presentation.favorites

import org.example.recipeapp.domain.model.FavoriteRecipe

data class FavoritesState(
    val items: List<FavoriteRecipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface FavoritesIntent {
    data object Load : FavoritesIntent
    data class Remove(val id: Int) : FavoritesIntent
    data class OpenDetail(val id: Int) : FavoritesIntent
}

sealed interface FavoritesEffect {
    data class ShowError(val message: String) : FavoritesEffect
    data class NavigateToDetail(val id: Int) : FavoritesEffect
}
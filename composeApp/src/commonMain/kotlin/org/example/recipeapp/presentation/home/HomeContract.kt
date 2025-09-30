package org.example.recipeapp.presentation.home

import org.example.recipeapp.domain.model.Recipe


data class HomeState(
    val featuredRecipes: List<Recipe> = emptyList(),
    val popularRecipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface HomeIntent {
    data object LoadRecipes : HomeIntent
    data object Retry : HomeIntent
}

sealed interface HomeEffect {
    data class ShowError(val message: String) : HomeEffect
}

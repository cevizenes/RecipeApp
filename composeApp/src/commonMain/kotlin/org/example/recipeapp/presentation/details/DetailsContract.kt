package org.example.recipeapp.presentation.details

import org.example.recipeapp.domain.model.RecipeDetail

data class DetailsState(
    val recipe: RecipeDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false
)

sealed interface DetailsIntent {
    data class LoadRecipe(val id: Int) : DetailsIntent
    data object ToggleFavorite : DetailsIntent
    data object Retry : DetailsIntent
}

sealed interface DetailsEffect {
    data class ShowError(val message: String) : DetailsEffect
    data class ShowMessage(val message: String) : DetailsEffect
}
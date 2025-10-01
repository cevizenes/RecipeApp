package org.example.recipeapp.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import org.example.recipeapp.domain.model.FavoriteRecipe
import org.example.recipeapp.domain.repository.FavoritesRepository
import org.example.recipeapp.domain.usecase.ObserveFavoritesUseCase

class FavoritesViewModel(
    observeFavoritesUseCase: ObserveFavoritesUseCase,
) : ViewModel() {
    val favorites = observeFavoritesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
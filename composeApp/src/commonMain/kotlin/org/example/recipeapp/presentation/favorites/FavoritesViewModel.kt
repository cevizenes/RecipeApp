package org.example.recipeapp.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.recipeapp.domain.usecase.ObserveFavoritesUseCase
import org.example.recipeapp.domain.usecase.RemoveFavoriteUseCase

class FavoritesViewModel(
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState(isLoading = true))
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    private val _effect = Channel<FavoritesEffect>(Channel.BUFFERED)
    val effect: Flow<FavoritesEffect> = _effect.receiveAsFlow()

    init {
        onIntent(FavoritesIntent.Load)
    }

    fun onIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.Load -> observe()
            is FavoritesIntent.Remove -> remove(intent.id)
            is FavoritesIntent.OpenDetail -> navigateToDetail(intent.id)
        }
    }

    private fun observe() {
        viewModelScope.launch {
            observeFavoritesUseCase()
                .onStart { _state.update { it.copy(isLoading = true, error = null) } }
                .catch { error ->
                    _state.update { it.copy(isLoading = false, error = error.message ?: "Unknown error") }
                    _effect.send(FavoritesEffect.ShowError(error.message ?: "Unknown error"))
                }
                .collect { list ->
                    _state.update { it.copy(items = list, isLoading = false, error = null) }
                }
        }
    }

    private fun remove(id: Int) {
        viewModelScope.launch {
            runCatching { removeFavoriteUseCase(id) }
                .onFailure { error ->
                    _effect.send(FavoritesEffect.ShowError(error.message ?: "Remove failed"))
                }
        }
    }

    private fun navigateToDetail(id: Int) {
        viewModelScope.launch {
            _effect.send(FavoritesEffect.NavigateToDetail(id))
        }
    }
}
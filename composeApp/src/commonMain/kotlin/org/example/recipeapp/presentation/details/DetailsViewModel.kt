package org.example.recipeapp.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.recipeapp.core.util.Result
import org.example.recipeapp.domain.usecase.GetRecipeDetailUseCase

class DetailsViewModel(
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DetailsState())
    val state: StateFlow<DetailsState> = _state.asStateFlow()

    private val _effect = Channel<DetailsEffect>(Channel.BUFFERED)
    val effect: Flow<DetailsEffect> = _effect.receiveAsFlow()

    private var currentRecipeId: Int? = null

    fun onIntent(intent: DetailsIntent) {
        when (intent) {
            is DetailsIntent.LoadRecipe -> {
                currentRecipeId = intent.id
                loadRecipe(intent.id)
            }
            is DetailsIntent.Retry -> {
                currentRecipeId?.let { loadRecipe(it) }
            }
            is DetailsIntent.ToggleFavorite -> toggleFavorite()
        }
    }

    private fun loadRecipe(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            when (val result = getRecipeDetailUseCase(id)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        recipe = result.data,
                        isLoading = false,
                        error = null
                    )
                }
                is Result.Error -> {
                    val errorMsg = result.message ?: result.exception.message ?: "Unknown error"
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = errorMsg
                    )
                    _effect.send(DetailsEffect.ShowError(errorMsg))
                }
                is Result.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }

    private fun toggleFavorite() {
        viewModelScope.launch {
            val newValue = !_state.value.isFavorite
            _state.value = _state.value.copy(isFavorite = newValue)

            val message = if (newValue) "Added to favorites" else "Removed from favorites"
            _effect.send(DetailsEffect.ShowMessage(message))
        }
    }
}
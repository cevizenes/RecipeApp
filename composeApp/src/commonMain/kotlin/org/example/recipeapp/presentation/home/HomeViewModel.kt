package org.example.recipeapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.recipeapp.core.util.Result
import org.example.recipeapp.domain.usecase.GetRandomRecipesUseCase

class HomeViewModel(
    private val getRandomRecipesUseCase: GetRandomRecipesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>(Channel.BUFFERED)
    val effect: Flow<HomeEffect> = _effect.receiveAsFlow()

    init {
        onIntent(HomeIntent.LoadRecipes)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadRecipes -> loadRecipes()
            is HomeIntent.Retry -> loadRecipes()
        }
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            when (val result = getRandomRecipesUseCase(15)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        featuredRecipes = result.data.take(1),
                        popularRecipes = result.data.drop(1),
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
                    _effect.send(HomeEffect.ShowError(errorMsg))
                }

                is Result.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }
}
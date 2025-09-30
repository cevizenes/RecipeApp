package org.example.recipeapp.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.recipeapp.core.util.Result
import org.example.recipeapp.domain.usecase.SearchRecipesUseCase

class SearchViewModel(
    private val searchRecipesUseCase: SearchRecipesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _effect = Channel<SearchEffect>(Channel.BUFFERED)
    val effect: Flow<SearchEffect> = _effect.receiveAsFlow()

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> {
                _state.value = _state.value.copy(query = intent.query, error = null)
            }
            is SearchIntent.Search -> performSearch(_state.value.query)
            is SearchIntent.QuickSearch -> {
                _state.value = _state.value.copy(query = intent.query)
                performSearch(intent.query)
            }
            is SearchIntent.ClearSearch -> {
                _state.value = SearchState()
            }
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            when (val result = searchRecipesUseCase(query)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        recipes = result.data,
                        isLoading = false,
                        hasSearched = true,
                        error = null
                    )
                }
                is Result.Error -> {
                    val errorMsg = result.message ?: result.exception.message ?: "Unknown error"
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = errorMsg
                    )
                    _effect.send(SearchEffect.ShowError(errorMsg))
                }
                is Result.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }
}
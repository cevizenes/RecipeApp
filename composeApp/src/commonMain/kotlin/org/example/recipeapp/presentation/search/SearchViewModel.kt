package org.example.recipeapp.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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

    private var searchJob: Job? = null

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> {
                _state.value = _state.value.copy(query = intent.query, error = null)

                searchJob?.cancel()

                val q = intent.query.trim()
                if (q.isEmpty()) {
                    _state.value = SearchState()
                    return
                }

                _state.value = _state.value.copy(isLoading = true)

                searchJob = viewModelScope.launch {
                    delay(350)
                    performSearch(query = q, type = null)
                }
            }
            is SearchIntent.Search -> performSearch(query = _state.value.query, type = null)
            is SearchIntent.QuickSearch -> {
                _state.value = _state.value.copy(query = intent.query)
                performSearch(query = intent.query, type = null)
            }
            is SearchIntent.ClearSearch -> {
                _state.value = SearchState()
            }
            is SearchIntent.SearchByType -> {
                _state.value = _state.value.copy(hasSearched = false, error = null)
                performSearch(query = null, type = intent.type)
            }
        }
    }

    private fun performSearch(query: String?, type: String?) {
        if ((query == null || query.isBlank()) && type.isNullOrBlank()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            when (val result = searchRecipesUseCase(
                query = query,
                cuisine = null,
                diet = null,
                type = type,
                maxReadyTime = null
            )) {
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
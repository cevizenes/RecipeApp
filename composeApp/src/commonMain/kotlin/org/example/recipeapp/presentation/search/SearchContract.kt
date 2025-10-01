package org.example.recipeapp.presentation.search

import org.example.recipeapp.domain.model.Recipe

data class SearchState(
    val query: String = "",
    val recipes: List<Recipe> = emptyList(),
    val recentSearches: List<String> = listOf("Pasta", "Chicken", "Pizza"),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false
)

sealed interface SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent
    data object Search : SearchIntent
    data class QuickSearch(val query: String) : SearchIntent
    data class SearchByType(val type: String) : SearchIntent
    data object ClearSearch : SearchIntent
}

sealed interface SearchEffect {
    data class ShowError(val message: String) : SearchEffect
}
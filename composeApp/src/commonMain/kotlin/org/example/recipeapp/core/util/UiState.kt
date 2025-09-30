package org.example.recipeapp.core.util

data class UiState<out T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val hasError: Boolean get() = error != null
    val hasData: Boolean get() = data != null
}

fun <T> Result<T>.toUiState(): UiState<T> = when (this) {
    is Result.Success -> UiState(data = data, isLoading = false, error = null)
    is Result.Error -> UiState(data = null, isLoading = false, error = message ?: exception.message)
    is Result.Loading -> UiState(data = null, isLoading = true, error = null)
}
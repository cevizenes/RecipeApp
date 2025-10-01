package org.example.recipeapp.presentation.search

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.example.recipeapp.presentation.details.DetailScreen
import org.koin.compose.koinInject

data class SearchByCategoryScreen(
    val type: String,
) : Screen {
    @Composable
    override fun Content() {
        val searchViewModel = koinInject<SearchViewModel>()
        val navigator = LocalNavigator.current
        val state by searchViewModel.state.collectAsState()
        val snackBarHostState = remember { SnackbarHostState() }

        LaunchedEffect(type) {
            searchViewModel.onIntent(
                SearchIntent.SearchByType(type)
            )
        }

        LaunchedEffect(Unit) {
            searchViewModel.effect.collect { effect ->
                when (effect) {
                    is SearchEffect.ShowError -> {
                        snackBarHostState.showSnackbar(
                            message = effect.message,
                            withDismissAction = true,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            }

        }

        SearchContent(
            state = state,
            onIntent = searchViewModel::onIntent,
            onRecipeClick = { recipeId ->
                navigator?.push(DetailScreen(recipeId))
            },
            snackBarHostState = snackBarHostState
        )

    }
}

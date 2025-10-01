package org.example.recipeapp.presentation.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.example.recipeapp.presentation.details.DetailScreen
import org.example.recipeapp.ui.components.RecipeCard
import org.koin.compose.koinInject
import kotlin.math.round

object FavoritesScreen : Screen {
    @Composable
    override fun Content() {
        val favoritesViewModel = koinInject<FavoritesViewModel>()
        val state by favoritesViewModel.state.collectAsState()
        val navigator = LocalNavigator.current
        val snackBarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            favoritesViewModel.effect.collect { effect ->
                when (effect) {
                    is FavoritesEffect.NavigateToDetail -> navigator?.push(DetailScreen(effect.id))
                    is FavoritesEffect.ShowError -> snackBarHostState.showSnackbar(
                        message = effect.message,
                        withDismissAction = true
                    )
                }
            }
        }

        Box(Modifier.fillMaxSize()) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error != null -> {
                    Column(
                        Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.error!!)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { favoritesViewModel.onIntent(FavoritesIntent.Load) }) {
                            Text(
                                "Retry"
                            )
                        }
                    }
                }

                state.items.isEmpty() -> {
                    Text("No favorites yet", Modifier.align(Alignment.Center))
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.items) { fav ->
                            val scoreOutOfFive = fav.score?.div(20.0)
                                RecipeCard(
                                    title = fav.title,
                                    subtitle = "${fav.readyInMinutes ?: "?"} min · ${formatScore(scoreOutOfFive)}",
                                    imageUrl = fav.image,
                                    onClick = {
                                        favoritesViewModel.onIntent(
                                            FavoritesIntent.OpenDetail(
                                                fav.id
                                            )
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                        }
                    }
                }
            }

            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

private fun formatScore(scoreOutOfFive: Double?) = scoreOutOfFive?.let { value ->
    if (value.isFinite()) {
        val rounded = round(value * 10) / 10.0
        val scoreStr = rounded.toString()
        if (rounded == round(rounded) && !scoreStr.contains('.')) {
            "$scoreStr.0★"
        } else {
            "$scoreStr★"
        }
    } else {
        "N/A"
    }
} ?: "N/A"
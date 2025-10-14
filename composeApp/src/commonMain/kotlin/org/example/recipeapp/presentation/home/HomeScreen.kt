package org.example.recipeapp.presentation.home

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import org.example.recipeapp.presentation.details.DetailScreen
import org.example.recipeapp.presentation.home.HomeEffect.*
import org.example.recipeapp.presentation.search.SearchByCategoryScreen
import org.example.recipeapp.ui.components.RecipeCard
import org.example.recipeapp.ui.components.SectionTitle
import org.koin.compose.viewmodel.koinViewModel

object HomeScreen : Screen {
    @Composable
    override fun Content() {
        val homeViewModel: HomeViewModel = koinViewModel()
        val state by homeViewModel.state.collectAsState()
        val navigator = LocalNavigator.current

        val snackBarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            homeViewModel.effect.collect { effect ->
                when (effect) {
                    is ShowError -> {
                        snackBarHostState.showSnackbar(
                            message = effect.message,
                            withDismissAction = true,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            }
        }

        HomeContent(
            state = state,
            onIntent = homeViewModel::onIntent,
            onRecipeClick = { recipeId ->
                navigator?.push(DetailScreen(recipeId))
            },
            snackBarHostState = snackBarHostState,
            onCategoryClick = { type ->
                navigator?.push(SearchByCategoryScreen(type))
            }
        )
    }
}

@Composable
private fun HomeContent(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    onRecipeClick: (Int) -> Unit,
    snackBarHostState: SnackbarHostState,
    onCategoryClick: (String) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(top = 24.dp, bottom = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {

                if (state.featuredRecipes.isNotEmpty()) {
                    item {
                        val featured = state.featuredRecipes.first()
                        RecipeCard(
                            title = featured.title,
                            subtitle = "${featured.displayTime} · ${featured.displayServings}",
                            badge = "FEATURED RECIPE",
                            imageUrl = featured.image,
                            onClick = { onRecipeClick(featured.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }

                item { SectionTitle("Categories") }
                item {
                    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                        val mapToType = mapOf(
                            "Breakfast" to "breakfast",
                            "Lunch" to "lunch",
                            "Dinner" to "dinner",
                            "Snack" to "snack"
                        )

                        listOf("Breakfast", "Lunch", "Dinner", "Snack").forEach { category ->
                            SuggestionChip(
                                onClick = {
                                    val type = mapToType[category] ?: category.lowercase()
                                    onCategoryClick(type)
                                },
                                label = { Text(category, maxLines = 1) },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                item { SectionTitle("Popular Recipes") }

                if (state.popularRecipes.isNotEmpty()) {
                    items(state.popularRecipes) { recipe ->
                        Spacer(Modifier.height(12.dp))
                        RecipeCard(
                            title = recipe.title,
                            subtitle = "${recipe.displayTime} · ${recipe.displayScore}",
                            imageUrl = recipe.image,
                            onClick = { onRecipeClick(recipe.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }

                if (state.error != null) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error: ${state.error}",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { onIntent(HomeIntent.Retry) }) {
                                Text("Retry")
                            }
                        }
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

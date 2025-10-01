package org.example.recipeapp.presentation.search

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import org.example.recipeapp.presentation.details.DetailScreen
import org.example.recipeapp.presentation.search.SearchEffect.*
import org.example.recipeapp.ui.components.FilterChip
import org.example.recipeapp.ui.components.RecipeCard
import org.example.recipeapp.ui.components.SectionTitle
import org.koin.compose.koinInject

object SearchScreen : Screen {
    @Composable
    override fun Content() {
        val searchViewModel = koinInject<SearchViewModel>()
        val state by searchViewModel.state.collectAsState()
        val navigator = LocalNavigator.current

        val snackBarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            searchViewModel.effect.collect { effect ->
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchContent(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit,
    onRecipeClick: (Int) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        OutlinedTextField(
            value = state.query,
            onValueChange = { onIntent(SearchIntent.QueryChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            placeholder = { Text("Search recipes") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = {
                if (state.query.isNotEmpty()) {
                    IconButton(onClick = { onIntent(SearchIntent.ClearSearch) }) {
                        Icon(Icons.Default.Clear, "Clear")
                    }
                }
            },
            singleLine = true,
            keyboardActions = KeyboardActions(
                onSearch = { onIntent(SearchIntent.Search) }
            )
        )

        if (state.hasSearched && state.recipes.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "${state.recipes.size} recipes found",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(state.recipes) { recipe ->
                    RecipeCard(
                        title = recipe.title,
                        subtitle = "${recipe.displayTime} · ${recipe.displayScore}",
                        imageUrl = recipe.image,
                        onClick = { onRecipeClick(recipe.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else if (state.hasSearched && state.recipes.isEmpty() && !state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "No recipes found for \"${state.query}\"",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { SectionTitle("Recent Searches") }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.recentSearches.forEach {
                            FilterChip(
                                label = it,
                                selected = state.query == it,
                                onClick = { onIntent(SearchIntent.QuickSearch(it)) }
                            )
                        }
                    }
                }
                item { SectionTitle("Popular Searches") }
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "Quick Meals",
                            "Healthy Recipes",
                            "Desserts",
                            "Vegetarian",
                            "Family Dinners",
                            "Holiday Recipes"
                        ).forEach { p ->
                            FilterChip(
                                label = p,
                                selected = false,
                                onClick = { onIntent(SearchIntent.QuickSearch(p)) }
                            )
                        }
                    }
                }
            }
        }

        if (state.error != null) {
            Snackbar(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(state.error)
            }
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )
    }
}

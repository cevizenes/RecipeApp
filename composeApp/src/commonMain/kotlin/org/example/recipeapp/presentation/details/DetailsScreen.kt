package org.example.recipeapp.presentation.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.example.recipeapp.ui.components.SectionTitle
import org.koin.compose.koinInject

data class DetailScreen(val recipeId: Int) : Screen {
    @Composable
    override fun Content() {
        val detailsViewModel = koinInject<DetailsViewModel>()
        val state by detailsViewModel.state.collectAsState()

        LaunchedEffect(recipeId) {
            detailsViewModel.onIntent(DetailsIntent.LoadRecipe(recipeId))
        }

        LaunchedEffect(Unit) {
            detailsViewModel.effect.collect { effect ->
                when (effect) {
                    is DetailsEffect.ShowError -> {
                    }

                    is DetailsEffect.ShowMessage -> {
                    }
                }
            }
        }

        DetailContent(
            state = state,
            onIntent = detailsViewModel::onIntent
        )
    }
}

@Composable
private fun DetailContent(
    state: DetailsState,
    onIntent: (DetailsIntent) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.recipe != null) {
            val recipe = state.recipe

            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            recipe.title,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { onIntent(DetailsIntent.ToggleFavorite) }
                        ) {
                            Icon(
                                if (state.isFavorite) Icons.Filled.Favorite
                                else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (state.isFavorite)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Info pills
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        recipe.readyInMinutes?.let { InfoPill("$it min") }
                        recipe.servings?.let { InfoPill("$it servings") }
                        recipe.nutrition?.let { InfoPill("${it.calories.toInt()} cal") }
                    }
                }

                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (recipe.vegetarian) AssistChip(
                            onClick = {},
                            label = { Text("Vegetarian") })
                        if (recipe.vegan) AssistChip(onClick = {}, label = { Text("Vegan") })
                        if (recipe.glutenFree) AssistChip(
                            onClick = {},
                            label = { Text("Gluten Free") })
                        recipe.cuisines.forEach { cuisine ->
                            AssistChip(onClick = {}, label = { Text(cuisine) })
                        }
                    }
                }

                if (recipe.ingredients.isNotEmpty()) {
                    item { SectionTitle("Ingredients") }
                    items(recipe.ingredients) { ingredient ->
                        Text("• ${ingredient.original}")
                    }
                }

                if (recipe.steps.isNotEmpty()) {
                    item { SectionTitle("Steps") }
                    items(recipe.steps) { step ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "${step.number}.",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                step.instruction,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                recipe.nutrition?.let { nutrition ->
                    item { SectionTitle("Nutrition Information") }
                    items(
                        listOf(
                            "Calories" to "${nutrition.calories.toInt()}",
                            "Fat" to "${nutrition.fat.toInt()}g",
                            "Protein" to "${nutrition.protein.toInt()}g",
                            "Carbohydrates" to "${nutrition.carbs.toInt()}g"
                        )
                    ) { (key, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(key)
                            Text(value, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        if (state.error != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Error: ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
                Text("Retry")
            }
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
}


@Composable
private fun InfoPill(text: String) {
    AssistChip(onClick = {}, label = { Text(text) })
}
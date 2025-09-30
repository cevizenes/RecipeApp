package org.example.recipeapp.presentation.home

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import org.example.recipeapp.presentation.details.DetailScreen
import org.example.recipeapp.screens.filters.FiltersScreen
import org.example.recipeapp.ui.components.RecipeCard
import org.example.recipeapp.ui.components.SectionTitle
import org.jetbrains.compose.ui.tooling.preview.Preview

object HomeScreen : Screen {
    @Composable
    override fun Content() {
        HomeContent()
    }
}

@Composable
private fun HomeContent() {
    val nav = LocalNavigator.current
    LazyColumn(
        contentPadding = PaddingValues(bottom = 100.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Recipes", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = { nav?.push(FiltersScreen) }) {
                    Text("⛭")
                }
            }
        }
        item {
            RecipeCard(
                title = "Creamy Tomato Pasta",
                subtitle = "45 min · Serves 4 · Easy",
                badge = "FEATURED RECIPE",
                onClick = { nav?.push(DetailScreen("Creamy Tomato Pasta")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(16.dp))
        }
        item { SectionTitle("Categories") }
        item {
            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                listOf("Breakfast", "Lunch", "Dinner", "Snack").forEachIndexed { idx, s ->
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(s, maxLines = 1)
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        item { SectionTitle("Popular Recipes") }
        items((1..6).map { "Recipe $it" }) { r ->
            Spacer(Modifier.height(12.dp))
            RecipeCard(
                title = r,
                subtitle = "20–40 min · 4.2★",
                onClick = { nav?.push(DetailScreen(r)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
@Preview
fun HomeScreenPreview() {
    MaterialTheme {
        HomeContent()
    }
}

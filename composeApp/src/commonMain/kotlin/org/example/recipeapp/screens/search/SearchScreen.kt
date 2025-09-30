package org.example.recipeapp.screens.search

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.recipeapp.ui.components.FilterChip
import org.example.recipeapp.ui.components.SectionTitle

object SearchScreen : Screen {
    @Composable
    override fun Content() {
        SearchContent()
    }
}

@Composable
private fun SearchContent() {
    var query by remember { mutableStateOf("Pasta") }
    var selected by remember { mutableStateOf(setOf("Vegetarian", "Vegan")) }
    val popular = listOf(
        "Quick Meals",
        "Healthy Recipes",
        "Desserts",
        "Vegetarian",
        "Family Dinners",
        "Holiday Recipes"
    )

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search recipes") }
            )
        }
        item { SectionTitle("Recent Searches") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Pasta", "Chicken", "Pizza").forEach {
                    FilterChip(label = it, selected = query == it, onClick = { query = it })
                }
            }
        }
        item { SectionTitle("Popular Searches") }
        item {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                popular.forEach { p ->
                    FilterChip(
                        label = p,
                        selected = selected.contains(p),
                        onClick = {
                            selected = selected.toMutableSet().apply {
                                if (!add(p)) remove(p)
                            }
                        }
                    )
                }
            }
        }
    }
}

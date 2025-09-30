package org.example.recipeapp.screens.filters

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.recipeapp.ui.components.FilterChip
import org.example.recipeapp.ui.components.PrimaryButton
import org.example.recipeapp.ui.components.SectionTitle

object FiltersScreen : Screen {
    @Composable
    override fun Content() {
        FiltersContent()
    }
}

@Composable
fun FiltersContent() {
    var cuisine by remember { mutableStateOf(setOf("Mexican")) }
    var diet by remember { mutableStateOf(setOf("Gluten-Free")) }
    var time by remember { mutableStateOf(setOf("30 min")) }
    var difficulty by remember { mutableStateOf(setOf<String>()) }

    fun toggle(set: Set<String>, value: String) =
        set.toMutableSet().apply { if (!add(value)) remove(value) }.toSet()

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Filters", style = MaterialTheme.typography.titleLarge)
            SectionTitle("Cuisine")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("Italian", "Mexican", "Indian").forEach {
                    FilterChip(it, it in cuisine, onClick = { cuisine = toggle(cuisine, it) })
                }
            }
            SectionTitle("Diet")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("Vegetarian", "Vegan", "Gluten-Free").forEach {
                    FilterChip(it, it in diet, onClick = { diet = toggle(diet, it) })
                }
            }
            SectionTitle("Time")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("30 min", "1 hour", "2 hours").forEach {
                    FilterChip(it, it in time, onClick = { time = toggle(time, it) })
                }
            }
            SectionTitle("Difficulty")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("Easy", "Medium", "Hard").forEach {
                    FilterChip(
                        it,
                        it in difficulty,
                        onClick = { difficulty = toggle(difficulty, it) })
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "${(cuisine + diet + time + difficulty).size} filters applied",
                color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
            )
        }
        Column(Modifier.padding(20.dp)) {
            PrimaryButton(text = "Apply Filters", onClick = { /* pass back result */ })
        }
    }
}
package org.example.recipeapp.screens.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.example.recipeapp.ui.components.SectionTitle

data class DetailScreen(val title: String) : Screen {
    @Composable
    override fun Content() {
        DetailContent(title)
    }
}

@Composable
private fun DetailContent(title: String) {
    val ingredients = listOf(
        "1 lb pasta", "1 tbsp olive oil", "2 cloves garlic, minced", "1 onion, chopped",
        "28 oz crushed tomatoes", "1 cup heavy cream", "1/2 cup grated Parmesan"
    )
    val steps = listOf(
        "Cook pasta according to package directions. Drain and set aside.",
        "Heat olive oil, add garlic and onion until softened.",
        "Stir in tomatoes and simmer 15 minutes.",
        "Reduce heat, add cream and Parmesan. Season.",
        "Add pasta to sauce and toss to combine."
    )

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text(title, style = MaterialTheme.typography.titleLarge) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoPill("45 min")
                InfoPill("4 servings")
                InfoPill("550 cal")
            }
        }
        item { SectionTitle("Ingredients") }
        items(ingredients) { Text("• $it") }
        item { SectionTitle("Steps") }
        items(steps.withIndex().toList()) { (idx, s) ->
            Text("${idx + 1}. $s")
        }
        item { SectionTitle("Nutrition Information") }
        items(
            listOf(
                "Calories" to "550",
                "Fat" to "30g",
                "Protein" to "20g",
                "Carbohydrates" to "50g"
            )
        ) { (k, v) ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(k); Text(v)
            }
        }
        item { SectionTitle("Similar Recipes") }
        items(listOf("Pasta Primavera", "Alfredo Pasta")) { r ->
            Spacer(Modifier.height(8.dp))
            Text(r)
        }
    }
}

@Composable
private fun InfoPill(text: String) {
    AssistChip(onClick = {}, label = { Text(text) })
}
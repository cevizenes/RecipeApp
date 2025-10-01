package org.example.recipeapp.presentation.favorites

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import org.example.recipeapp.presentation.details.DetailScreen
import org.example.recipeapp.ui.components.RecipeCard
import org.koin.compose.koinInject
import kotlin.math.round

object FavoritesScreen : Screen {
    @Composable
    override fun Content() {
        val favoritesViewModel = koinInject<FavoritesViewModel>()
        val list by favoritesViewModel.favorites.collectAsState()
        val navigator = LocalNavigator.current

        if (list.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No favorites yet")
            }
            return
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(list) { fav ->

                val scoreOutOfFive = fav.score?.div(20.0)
                val formattedScore = scoreOutOfFive?.let { value ->
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

                RecipeCard(
                    title = fav.title,
                    subtitle = "${fav.readyInMinutes ?: "?"} min · $formattedScore",                    imageUrl = fav.image,
                    onClick = { navigator?.push(DetailScreen(fav.id)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
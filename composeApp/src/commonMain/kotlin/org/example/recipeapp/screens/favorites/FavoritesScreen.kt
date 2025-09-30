package org.example.recipeapp.screens.favorites

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.recipeapp.ui.components.PrimaryButton

object FavoritesScreen : Screen {
    @Composable
    override fun Content() {
        FavoritesContent()
    }
}

@Composable
fun FavoritesContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("You don't have any favorite recipes yet", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(8.dp))

        Text(
            "Your saved recipes will appear here.",
            color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
        )

        Spacer(Modifier.height(24.dp))

        PrimaryButton(text = "Discover recipes")
    }
}
package org.example.recipeapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.example.recipeapp.screens.favorites.FavoritesScreen
import org.example.recipeapp.screens.home.HomeScreen
import org.example.recipeapp.screens.search.SearchScreen
import org.example.recipeapp.ui.components.BottomBar
import org.example.recipeapp.ui.components.BottomItem


@Composable
fun RootNav() {
    var selected by remember { mutableStateOf(0) }
    val tabs = listOf(
        BottomItem("Home", Icons.Default.Home, HomeScreen),
        BottomItem("Search", Icons.Default.Search, SearchScreen),
        BottomItem("Favorites", Icons.Default.Favorite, FavoritesScreen),
    )

    Scaffold(
        bottomBar = {
            BottomBar(
                items = tabs,
                selectedIndex = selected,
                onSelect = { selected = it }
            )
        }
    ) { paddings ->
        Navigator(tabs[selected].screen) { nav ->
            SlideTransition(nav)
        }
    }
}
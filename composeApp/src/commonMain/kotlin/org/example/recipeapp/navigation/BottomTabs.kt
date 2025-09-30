package org.example.recipeapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import org.example.recipeapp.screens.favorites.FavoritesScreen
import org.example.recipeapp.screens.home.HomeScreen
import org.example.recipeapp.screens.search.SearchScreen

object HomeTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val homePainterIcon = rememberVectorPainter(Icons.Outlined.Home)
            return TabOptions(
                index = 0u,
                title = "Home",
                icon = homePainterIcon
            )
        }

    @Composable
    override fun Content() {
        Navigator(HomeScreen) { SlideTransition(navigator = it) }
    }
}

object SearchTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val searchPainterIcon = rememberVectorPainter(Icons.Outlined.Search)
            return TabOptions(
                index = 0u,
                title = "Search",
                icon = searchPainterIcon
            )
        }

    @Composable
    override fun Content() {
        Navigator(SearchScreen) { SlideTransition(navigator = it) }
    }
}

object FavoritesTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val favoritesPainterIcon = rememberVectorPainter(Icons.Outlined.FavoriteBorder)
            return TabOptions(
                index = 0u,
                title = "Favorites",
                icon = favoritesPainterIcon
            )
        }

    @Composable
    override fun Content() {
        Navigator(FavoritesScreen) { SlideTransition(navigator = it) }
    }
}
package org.example.recipeapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import kotlin.requireNotNull


@Composable
fun RootNav() {
    TabNavigator(
        HomeTab
    ) { tabNavigator ->
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    val tabToSelectedIcon = mapOf(
                        HomeTab to Icons.Filled.Home,
                        SearchTab to Icons.Filled.Search,
                        FavoritesTab to Icons.Filled.Favorite
                    )

                    val allTabs = listOf(HomeTab, SearchTab, FavoritesTab)
                    allTabs.forEach { tab ->
                        val isSelected = tabNavigator.current == tab
                        val currentIconPainter = if (isSelected) rememberVectorPainter(
                            image = requireNotNull(tabToSelectedIcon[tab])
                        ) else tab.options.icon
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { tabNavigator.current = tab },
                            icon = {
                                if (currentIconPainter != null)
                                    Icon(
                                        painter = currentIconPainter,
                                        contentDescription = tab.options.title
                                    )
                            },
                            label = { Text(tab.options.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                CurrentTab()
            }
        }
    }
}
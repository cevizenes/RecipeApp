package org.example.recipeapp.ui.components


import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen

data class BottomItem(
    val title: String,
    val icon: ImageVector,
    val screen: Screen
)

@Composable
fun BottomBar(
    items: List<BottomItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onSelect(index) },
                label = { Text(item.title) },
                icon = { Icon(item.icon, contentDescription = item.title) }
            )
        }
    }
}


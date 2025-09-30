package org.example.recipeapp

import androidx.compose.runtime.*
import org.example.recipeapp.navigation.RootNav
import org.example.recipeapp.ui.theme.RecipeTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() = RecipeTheme { RootNav() }
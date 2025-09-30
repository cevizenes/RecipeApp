package org.example.recipeapp

import androidx.compose.ui.window.ComposeUIViewController

import org.koin.core.context.startKoin
import org.example.recipeapp.di.appModules

fun MainViewController() = ComposeUIViewController {
    initKoinIos()
    App()
}

private fun initKoinIos() {
    startKoin {
        properties(mapOf(
            "SPOONACULAR_API_KEY" to "34eddf106359410ba352ced0749915c5"
        ))
        modules(appModules)
    }
}
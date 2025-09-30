package org.example.recipeapp

import androidx.compose.ui.window.ComposeUIViewController

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.context.startKoin
import org.example.recipeapp.di.appModules

fun MainViewController() = ComposeUIViewController {
    App()
}

fun initKoin() {
    startKoin {
        modules(appModules)
    }
}
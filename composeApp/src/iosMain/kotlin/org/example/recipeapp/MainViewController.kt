package org.example.recipeapp

import androidx.compose.ui.window.ComposeUIViewController

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.context.startKoin
import org.example.recipeapp.di.appModules
import org.example.recipeapp.di.platformModule

fun MainViewController() = ComposeUIViewController {
    App()
}

fun initKoin() {
    startKoin {
        modules(appModules + platformModule)
    }
}
package org.example.recipeapp.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.example.recipeapp.db.DriverFactory

val platformModule = module {
    single { DriverFactory(androidContext()) }
}
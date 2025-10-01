package org.example.recipeapp.db

import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): AppDatabase {
    val driver = driverFactory.createDriver()
    return AppDatabase(driver)
}

val dbDispatcher = Dispatchers.Default
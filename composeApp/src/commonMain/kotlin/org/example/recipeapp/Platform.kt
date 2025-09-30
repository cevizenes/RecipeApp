package org.example.recipeapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
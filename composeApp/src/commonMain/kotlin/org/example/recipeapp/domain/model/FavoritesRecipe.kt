package org.example.recipeapp.domain.model

data class FavoriteRecipe(
    val id: Int,
    val title: String,
    val image: String?,
    val readyInMinutes: Int?,
    val score: Double?
)
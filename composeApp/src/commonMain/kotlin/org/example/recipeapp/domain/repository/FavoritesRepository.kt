package org.example.recipeapp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.recipeapp.domain.model.FavoriteRecipe

interface FavoritesRepository {
    fun observeAll(): Flow<List<FavoriteRecipe>>
    suspend fun isFavorite(id: Int): Boolean
    suspend fun add(f: FavoriteRecipe)
    suspend fun remove(id: Int)
    suspend fun toggle(f: FavoriteRecipe): Boolean
}
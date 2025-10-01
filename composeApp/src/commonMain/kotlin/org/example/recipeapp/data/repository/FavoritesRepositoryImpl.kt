package org.example.recipeapp.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.example.recipeapp.data.mapper.asDomain
import org.example.recipeapp.db.AppDatabase
import org.example.recipeapp.domain.model.FavoriteRecipe
import org.example.recipeapp.domain.repository.FavoritesRepository

class FavoritesRepositoryImpl(
    private val db: AppDatabase
) : FavoritesRepository {

    private val queries get() = db.favoritesQueries

    override fun observeAll(): Flow<List<FavoriteRecipe>> =
        queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.asDomain() } }

    override suspend fun isFavorite(id: Int): Boolean = withContext(Dispatchers.Default) {
        queries.selectById(id.toLong()).executeAsOneOrNull() != null
    }

    override suspend fun add(f: FavoriteRecipe) = withContext(Dispatchers.Default) {
        queries.insertOrReplace(
            id = f.id.toLong(),
            title = f.title,
            image = f.image,
            readyInMinutes = f.readyInMinutes?.toLong(),
            score = f.score
        )
    }

    override suspend fun remove(id: Int) = withContext(Dispatchers.Default) {
        queries.deleteById(id.toLong())
    }

    override suspend fun toggle(f: FavoriteRecipe): Boolean = withContext(Dispatchers.Default) {
        val exists = isFavorite(f.id)
        if (exists) remove(f.id) else add(f)
        !exists
    }
}
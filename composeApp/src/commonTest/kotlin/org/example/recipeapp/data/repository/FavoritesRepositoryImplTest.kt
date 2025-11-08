package org.example.recipeapp.data.repository

import app.cash.sqldelight.Query
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.recipeapp.db.AppDatabase
import org.example.recipeapp.db.Favorites
import org.example.recipeapp.db.FavoritesQueries
import org.example.recipeapp.domain.model.FavoriteRecipe
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FavoritesRepositoryImplTest {

    private val database = mockk<AppDatabase>()
    private val queries = mockk<FavoritesQueries>(relaxed = true)
    private val repository = FavoritesRepositoryImpl(database)

    init {
        every { database.favoritesQueries } returns queries
    }

    private val testFavoriteEntity = Favorites(
        id = 1L,
        title = "Test Recipe",
        image = "image.jpg",
        readyInMinutes = 30L,
        score = 4.5
    )

    private val testFavoriteRecipe = FavoriteRecipe(
        id = 1,
        title = "Test Recipe",
        image = "image.jpg",
        readyInMinutes = 30,
        score = 4.5
    )

    // Skipping observeAll test due to SQLDelight Flow complexity in mocking

    @Test
    fun `isFavorite should return true when recipe exists`() = runTest {
        // Given
        val recipeId = 1
        val mockQuery = mockk<Query<Favorites>>()
        every { queries.selectById(recipeId.toLong()) } returns mockQuery
        every { mockQuery.executeAsOneOrNull() } returns testFavoriteEntity

        // When
        val result = repository.isFavorite(recipeId)

        // Then
        assertTrue(result)
        verify(exactly = 1) { queries.selectById(recipeId.toLong()) }
    }

    @Test
    fun `isFavorite should return false when recipe does not exist`() = runTest {
        // Given
        val recipeId = 999
        val mockQuery = mockk<Query<Favorites>>()
        every { queries.selectById(recipeId.toLong()) } returns mockQuery
        every { mockQuery.executeAsOneOrNull() } returns null

        // When
        val result = repository.isFavorite(recipeId)

        // Then
        assertFalse(result)
        verify(exactly = 1) { queries.selectById(recipeId.toLong()) }
    }

    @Test
    fun `add should insert favorite into database`() = runTest {
        // Given
        every { 
            queries.insertOrReplace(
                id = testFavoriteRecipe.id.toLong(),
                title = testFavoriteRecipe.title,
                image = testFavoriteRecipe.image,
                readyInMinutes = testFavoriteRecipe.readyInMinutes?.toLong(),
                score = testFavoriteRecipe.score
            ) 
        } just runs

        // When
        repository.add(testFavoriteRecipe)

        // Then
        verify(exactly = 1) { 
            queries.insertOrReplace(
                id = testFavoriteRecipe.id.toLong(),
                title = testFavoriteRecipe.title,
                image = testFavoriteRecipe.image,
                readyInMinutes = testFavoriteRecipe.readyInMinutes?.toLong(),
                score = testFavoriteRecipe.score
            ) 
        }
    }

    @Test
    fun `remove should delete favorite from database`() = runTest {
        // Given
        val recipeId = 1
        every { queries.deleteById(recipeId.toLong()) } just runs

        // When
        repository.remove(recipeId)

        // Then
        verify(exactly = 1) { queries.deleteById(recipeId.toLong()) }
    }

    @Test
    fun `toggle should add favorite when it does not exist`() = runTest {
        // Given
        val mockQuery = mockk<Query<Favorites>>()
        every { queries.selectById(testFavoriteRecipe.id.toLong()) } returns mockQuery
        every { mockQuery.executeAsOneOrNull() } returns null
        every { 
            queries.insertOrReplace(
                id = any(),
                title = any(),
                image = any(),
                readyInMinutes = any(),
                score = any()
            ) 
        } just runs

        // When
        val result = repository.toggle(testFavoriteRecipe)

        // Then
        assertTrue(result)
        verify(exactly = 1) { 
            queries.insertOrReplace(
                id = testFavoriteRecipe.id.toLong(),
                title = testFavoriteRecipe.title,
                image = testFavoriteRecipe.image,
                readyInMinutes = testFavoriteRecipe.readyInMinutes?.toLong(),
                score = testFavoriteRecipe.score
            ) 
        }
    }

    @Test
    fun `toggle should remove favorite when it exists`() = runTest {
        // Given
        val mockQuery = mockk<Query<Favorites>>()
        every { queries.selectById(testFavoriteRecipe.id.toLong()) } returns mockQuery
        every { mockQuery.executeAsOneOrNull() } returns testFavoriteEntity
        every { queries.deleteById(testFavoriteRecipe.id.toLong()) } just runs

        // When
        val result = repository.toggle(testFavoriteRecipe)

        // Then
        assertFalse(result)
        verify(exactly = 1) { queries.deleteById(testFavoriteRecipe.id.toLong()) }
    }

    @Test
    fun `add should handle null values correctly`() = runTest {
        // Given
        val favoriteWithNulls = FavoriteRecipe(
            id = 2,
            title = "Test Recipe 2",
            image = null,
            readyInMinutes = null,
            score = null
        )
        every { 
            queries.insertOrReplace(
                id = favoriteWithNulls.id.toLong(),
                title = favoriteWithNulls.title,
                image = null,
                readyInMinutes = null,
                score = null
            ) 
        } just runs

        // When
        repository.add(favoriteWithNulls)

        // Then
        verify(exactly = 1) { 
            queries.insertOrReplace(
                id = favoriteWithNulls.id.toLong(),
                title = favoriteWithNulls.title,
                image = null,
                readyInMinutes = null,
                score = null
            ) 
        }
    }
}


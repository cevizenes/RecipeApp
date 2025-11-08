package org.example.recipeapp.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.recipeapp.domain.repository.FavoritesRepository
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IsFavoriteUseCaseTest {

    private val repository = mockk<FavoritesRepository>()
    private val useCase = IsFavoriteUseCase(repository)

    @Test
    fun `invoke should return true when recipe is favorite`() = runTest {
        // Given
        val recipeId = 1
        coEvery { repository.isFavorite(recipeId) } returns true

        // When
        val result = useCase.invoke(recipeId)

        // Then
        assertTrue(result)
        coVerify(exactly = 1) { repository.isFavorite(recipeId) }
    }

    @Test
    fun `invoke should return false when recipe is not favorite`() = runTest {
        // Given
        val recipeId = 1
        coEvery { repository.isFavorite(recipeId) } returns false

        // When
        val result = useCase.invoke(recipeId)

        // Then
        assertFalse(result)
        coVerify(exactly = 1) { repository.isFavorite(recipeId) }
    }

    @Test
    fun `invoke should pass correct id to repository`() = runTest {
        // Given
        val recipeId = 999
        coEvery { repository.isFavorite(recipeId) } returns false

        // When
        useCase.invoke(recipeId)

        // Then
        coVerify(exactly = 1) { repository.isFavorite(recipeId) }
    }
}


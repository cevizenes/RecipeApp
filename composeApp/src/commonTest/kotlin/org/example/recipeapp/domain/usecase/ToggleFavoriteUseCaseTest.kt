package org.example.recipeapp.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.recipeapp.domain.model.FavoriteRecipe
import org.example.recipeapp.domain.repository.FavoritesRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ToggleFavoriteUseCaseTest {

    private val repository = mockk<FavoritesRepository>()
    private val useCase = ToggleFavoriteUseCase(repository)

    private val testFavorite = FavoriteRecipe(
        id = 1,
        title = "Test Recipe",
        image = "image.jpg",
        readyInMinutes = 30,
        score = 4.5
    )

    @Test
    fun `invoke should return true when favorite is added`() = runTest {
        // Given
        coEvery { repository.toggle(testFavorite) } returns true

        // When
        val result = useCase.invoke(testFavorite)

        // Then
        assertTrue(result)
        coVerify(exactly = 1) { repository.toggle(testFavorite) }
    }

    @Test
    fun `invoke should return false when favorite is removed`() = runTest {
        // Given
        coEvery { repository.toggle(testFavorite) } returns false

        // When
        val result = useCase.invoke(testFavorite)

        // Then
        assertFalse(result)
        coVerify(exactly = 1) { repository.toggle(testFavorite) }
    }

    @Test
    fun `invoke should pass correct favorite to repository`() = runTest {
        // Given
        val customFavorite = FavoriteRecipe(
            id = 999,
            title = "Custom Recipe",
            image = null,
            readyInMinutes = 60,
            score = 3.5
        )
        coEvery { repository.toggle(customFavorite) } returns true

        // When
        val result = useCase.invoke(customFavorite)

        // Then
        assertTrue(result)
        coVerify(exactly = 1) { repository.toggle(customFavorite) }
    }
}


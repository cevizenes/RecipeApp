package org.example.recipeapp.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.example.recipeapp.domain.model.FavoriteRecipe
import org.example.recipeapp.domain.repository.FavoritesRepository
import kotlin.test.Test

class AddFavoriteUseCaseTest {

    private val repository = mockk<FavoritesRepository>()
    private val useCase = AddFavoriteUseCase(repository)

    private val testFavorite = FavoriteRecipe(
        id = 1,
        title = "Test Recipe",
        image = "image.jpg",
        readyInMinutes = 30,
        score = 4.5
    )

    @Test
    fun `invoke should call repository add`() = runTest {
        // Given
        coEvery { repository.add(testFavorite) } just runs

        // When
        useCase.invoke(testFavorite)

        // Then
        coVerify(exactly = 1) { repository.add(testFavorite) }
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
        coEvery { repository.add(customFavorite) } just runs

        // When
        useCase.invoke(customFavorite)

        // Then
        coVerify(exactly = 1) { repository.add(customFavorite) }
    }
}


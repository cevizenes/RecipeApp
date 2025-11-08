package org.example.recipeapp.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.example.recipeapp.domain.repository.FavoritesRepository
import kotlin.test.Test

class RemoveFavoriteUseCaseTest {

    private val repository = mockk<FavoritesRepository>()
    private val useCase = RemoveFavoriteUseCase(repository)

    @Test
    fun `invoke should call repository remove`() = runTest {
        // Given
        val recipeId = 1
        coEvery { repository.remove(recipeId) } just runs

        // When
        useCase.invoke(recipeId)

        // Then
        coVerify(exactly = 1) { repository.remove(recipeId) }
    }

    @Test
    fun `invoke should pass correct id to repository`() = runTest {
        // Given
        val recipeId = 999
        coEvery { repository.remove(recipeId) } just runs

        // When
        useCase.invoke(recipeId)

        // Then
        coVerify(exactly = 1) { repository.remove(recipeId) }
    }
}


package org.example.recipeapp.domain.usecase

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.example.recipeapp.domain.model.FavoriteRecipe
import org.example.recipeapp.domain.repository.FavoritesRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ObserveFavoritesUseCaseTest {

    private val repository = mockk<FavoritesRepository>()
    private val useCase = ObserveFavoritesUseCase(repository)

    private val testFavorites = listOf(
        FavoriteRecipe(
            id = 1,
            title = "Test Recipe 1",
            image = "image1.jpg",
            readyInMinutes = 30,
            score = 4.5
        ),
        FavoriteRecipe(
            id = 2,
            title = "Test Recipe 2",
            image = "image2.jpg",
            readyInMinutes = 45,
            score = 4.0
        )
    )

    @Test
    fun `invoke should return flow from repository`() = runTest {
        // Given
        every { repository.observeAll() } returns flowOf(testFavorites)

        // When
        val flow = useCase.invoke()

        // Then
        flow.test {
            val result = awaitItem()
            assertEquals(testFavorites, result)
            awaitComplete()
        }
        verify(exactly = 1) { repository.observeAll() }
    }

    @Test
    fun `invoke should return empty flow when no favorites`() = runTest {
        // Given
        val emptyList = emptyList<FavoriteRecipe>()
        every { repository.observeAll() } returns flowOf(emptyList)

        // When
        val flow = useCase.invoke()

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            awaitComplete()
        }
        verify(exactly = 1) { repository.observeAll() }
    }

    @Test
    fun `invoke should emit multiple values from repository flow`() = runTest {
        // Given
        val firstList = listOf(testFavorites[0])
        val secondList = testFavorites
        every { repository.observeAll() } returns flowOf(firstList, secondList)

        // When
        val flow = useCase.invoke()

        // Then
        flow.test {
            assertEquals(firstList, awaitItem())
            assertEquals(secondList, awaitItem())
            awaitComplete()
        }
        verify(exactly = 1) { repository.observeAll() }
    }
}


package org.example.recipeapp.presentation.favorites

import app.cash.turbine.test
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.example.recipeapp.domain.model.FavoriteRecipe
import org.example.recipeapp.domain.usecase.ObserveFavoritesUseCase
import org.example.recipeapp.domain.usecase.RemoveFavoriteUseCase
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private lateinit var observeFavoritesUseCase: ObserveFavoritesUseCase
    private lateinit var removeFavoriteUseCase: RemoveFavoriteUseCase
    private lateinit var viewModel: FavoritesViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testFavorites = listOf(
        FavoriteRecipe(
            id = 1,
            title = "Favorite Recipe 1",
            image = "image1.jpg",
            readyInMinutes = 30,
            score = 4.5
        ),
        FavoriteRecipe(
            id = 2,
            title = "Favorite Recipe 2",
            image = "image2.jpg",
            readyInMinutes = 45,
            score = 4.0
        )
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        observeFavoritesUseCase = mockk()
        removeFavoriteUseCase = mockk()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should observe favorites automatically`() = runTest {
        // Given
        every { observeFavoritesUseCase() } returns flowOf(testFavorites)

        // When
        viewModel = FavoritesViewModel(observeFavoritesUseCase, removeFavoriteUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(exactly = 1) { observeFavoritesUseCase() }
    }

    @Test
    fun `Load intent should update state with favorites`() = runTest {
        // Given
        every { observeFavoritesUseCase() } returns flowOf(testFavorites)

        // When
        viewModel = FavoritesViewModel(observeFavoritesUseCase, removeFavoriteUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(testFavorites, state.items)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `Load intent should handle empty favorites list`() = runTest {
        // Given
        every { observeFavoritesUseCase() } returns flowOf(emptyList())

        // When
        viewModel = FavoritesViewModel(observeFavoritesUseCase, removeFavoriteUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state.items.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `Load intent should handle error`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        every { observeFavoritesUseCase() } returns kotlinx.coroutines.flow.flow {
            throw exception
        }

        // When
        viewModel = FavoritesViewModel(observeFavoritesUseCase, removeFavoriteUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("Database error", state.error)
    }

    @Test
    fun `Load intent should emit ShowError effect on error`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        every { observeFavoritesUseCase() } returns kotlinx.coroutines.flow.flow {
            throw exception
        }

        // When
        viewModel = FavoritesViewModel(observeFavoritesUseCase, removeFavoriteUseCase)

        // Then
        viewModel.effect.test {
            testDispatcher.scheduler.advanceUntilIdle()
            
            val effect = awaitItem()
            assertTrue(effect is FavoritesEffect.ShowError)
            assertEquals("Database error", (effect as FavoritesEffect.ShowError).message)
        }
    }

    @Test
    fun `Remove intent should call removeFavoriteUseCase`() = runTest {
        // Given
        every { observeFavoritesUseCase() } returns flowOf(testFavorites)
        coEvery { removeFavoriteUseCase(1) } just runs

        viewModel = FavoritesViewModel(observeFavoritesUseCase, removeFavoriteUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onIntent(FavoritesIntent.Remove(1))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { removeFavoriteUseCase(1) }
    }

    @Test
    fun `Remove intent should emit ShowError effect on failure`() = runTest {
        // Given
        every { observeFavoritesUseCase() } returns flowOf(testFavorites)
        coEvery { removeFavoriteUseCase(1) } throws RuntimeException("Remove failed")

        viewModel = FavoritesViewModel(observeFavoritesUseCase, removeFavoriteUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.effect.test {
            viewModel.onIntent(FavoritesIntent.Remove(1))
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val effect = awaitItem()
            assertTrue(effect is FavoritesEffect.ShowError)
            assertEquals("Remove failed", (effect as FavoritesEffect.ShowError).message)
        }
    }

    @Test
    fun `OpenDetail intent should emit NavigateToDetail effect`() = runTest {
        // Given
        every { observeFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(observeFavoritesUseCase, removeFavoriteUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.effect.test {
            viewModel.onIntent(FavoritesIntent.OpenDetail(1))
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val effect = awaitItem()
            assertTrue(effect is FavoritesEffect.NavigateToDetail)
            assertEquals(1, (effect as FavoritesEffect.NavigateToDetail).id)
        }
    }

    @Test
    fun `state should update when favorites flow emits new values`() = runTest {
        // Given
        val flow = kotlinx.coroutines.flow.MutableStateFlow(testFavorites)
        every { observeFavoritesUseCase() } returns flow

        // When
        viewModel = FavoritesViewModel(observeFavoritesUseCase, removeFavoriteUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val initialState = viewModel.state.value
        assertEquals(2, initialState.items.size)

        // Emit new value
        val updatedFavorites = listOf(testFavorites[0])
        flow.value = updatedFavorites
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val updatedState = viewModel.state.value
        assertEquals(1, updatedState.items.size)
    }
}


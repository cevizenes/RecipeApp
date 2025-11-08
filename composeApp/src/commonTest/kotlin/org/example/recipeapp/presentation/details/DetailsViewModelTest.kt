package org.example.recipeapp.presentation.details

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.example.recipeapp.core.util.Result
import org.example.recipeapp.domain.model.FavoriteRecipe
import org.example.recipeapp.domain.model.RecipeDetail
import org.example.recipeapp.domain.usecase.GetRecipeDetailUseCase
import org.example.recipeapp.domain.usecase.IsFavoriteUseCase
import org.example.recipeapp.domain.usecase.ToggleFavoriteUseCase
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    private lateinit var getRecipeDetailUseCase: GetRecipeDetailUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    private lateinit var isFavoriteUseCase: IsFavoriteUseCase
    private lateinit var viewModel: DetailsViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testRecipeDetail = RecipeDetail(
        id = 1,
        title = "Test Recipe Detail",
        image = "image.jpg",
        readyInMinutes = 30,
        servings = 4,
        summary = "Test summary",
        cuisines = listOf("Italian"),
        dishTypes = listOf("main course"),
        diets = emptyList(),
        instructions = "Test instructions",
        ingredients = emptyList(),
        steps = emptyList(),
        nutrition = null,
        vegetarian = false,
        vegan = false,
        glutenFree = false,
        aggregateLikes = 100,
        spoonacularScore = 80.0
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getRecipeDetailUseCase = mockk()
        toggleFavoriteUseCase = mockk()
        isFavoriteUseCase = mockk()
        viewModel = DetailsViewModel(getRecipeDetailUseCase, toggleFavoriteUseCase, isFavoriteUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be empty and not loading`() {
        // Then
        val state = viewModel.state.value
        assertNull(state.recipe)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertFalse(state.isFavorite)
    }

    @Test
    fun `LoadRecipe should update state with recipe on success`() = runTest {
        // Given
        val recipeId = 1
        coEvery { getRecipeDetailUseCase(recipeId) } returns Result.Success(testRecipeDetail)
        coEvery { isFavoriteUseCase(recipeId) } returns false

        // When
        viewModel.onIntent(DetailsIntent.LoadRecipe(recipeId))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(testRecipeDetail, state.recipe)
        assertFalse(state.isLoading)
        assertNull(state.error)
        coVerify(exactly = 1) { getRecipeDetailUseCase(recipeId) }
        coVerify(exactly = 1) { isFavoriteUseCase(recipeId) }
    }

    @Test
    fun `LoadRecipe should update favorite flag`() = runTest {
        // Given
        val recipeId = 1
        coEvery { getRecipeDetailUseCase(recipeId) } returns Result.Success(testRecipeDetail)
        coEvery { isFavoriteUseCase(recipeId) } returns true

        // When
        viewModel.onIntent(DetailsIntent.LoadRecipe(recipeId))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state.isFavorite)
    }

    @Test
    fun `LoadRecipe should update state with error on failure`() = runTest {
        // Given
        val recipeId = 1
        val exception = RuntimeException("Network error")
        val errorMessage = "Failed to load recipe"
        coEvery { getRecipeDetailUseCase(recipeId) } returns Result.Error(exception, errorMessage)

        // When
        viewModel.onIntent(DetailsIntent.LoadRecipe(recipeId))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
        assertNull(state.recipe)
    }

    @Test
    fun `LoadRecipe should emit ShowError effect on failure`() = runTest {
        // Given
        val recipeId = 1
        val exception = RuntimeException("Network error")
        val errorMessage = "Failed to load recipe"
        coEvery { getRecipeDetailUseCase(recipeId) } returns Result.Error(exception, errorMessage)

        // When
        viewModel.effect.test {
            viewModel.onIntent(DetailsIntent.LoadRecipe(recipeId))
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val effect = awaitItem()
            assertTrue(effect is DetailsEffect.ShowError)
            assertEquals(errorMessage, (effect as DetailsEffect.ShowError).message)
        }
    }

    @Test
    fun `Retry should reload the last recipe`() = runTest {
        // Given
        val recipeId = 1
        coEvery { getRecipeDetailUseCase(recipeId) } returns Result.Success(testRecipeDetail)
        coEvery { isFavoriteUseCase(recipeId) } returns false

        viewModel.onIntent(DetailsIntent.LoadRecipe(recipeId))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onIntent(DetailsIntent.Retry)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 2) { getRecipeDetailUseCase(recipeId) }
    }

    @Test
    fun `Retry should do nothing if no recipe was loaded before`() = runTest {
        // When
        viewModel.onIntent(DetailsIntent.Retry)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { getRecipeDetailUseCase(any()) }
    }

    @Test
    fun `ToggleFavorite should add to favorites when not favorite`() = runTest {
        // Given
        val recipeId = 1
        coEvery { getRecipeDetailUseCase(recipeId) } returns Result.Success(testRecipeDetail)
        coEvery { isFavoriteUseCase(recipeId) } returns false
        coEvery { toggleFavoriteUseCase(any()) } returns true

        viewModel.onIntent(DetailsIntent.LoadRecipe(recipeId))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.effect.test {
            viewModel.onIntent(DetailsIntent.ToggleFavorite)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertTrue(state.isFavorite)
            
            val effect = awaitItem()
            assertTrue(effect is DetailsEffect.ShowMessage)
            assertEquals("Added to favorites", (effect as DetailsEffect.ShowMessage).message)
        }
    }

    @Test
    fun `ToggleFavorite should remove from favorites when favorite`() = runTest {
        // Given
        val recipeId = 1
        coEvery { getRecipeDetailUseCase(recipeId) } returns Result.Success(testRecipeDetail)
        coEvery { isFavoriteUseCase(recipeId) } returns true
        coEvery { toggleFavoriteUseCase(any()) } returns false

        viewModel.onIntent(DetailsIntent.LoadRecipe(recipeId))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.effect.test {
            viewModel.onIntent(DetailsIntent.ToggleFavorite)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertFalse(state.isFavorite)
            
            val effect = awaitItem()
            assertTrue(effect is DetailsEffect.ShowMessage)
            assertEquals("Removed from favorites", (effect as DetailsEffect.ShowMessage).message)
        }
    }

    @Test
    fun `ToggleFavorite should do nothing if no recipe loaded`() = runTest {
        // When
        viewModel.onIntent(DetailsIntent.ToggleFavorite)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { toggleFavoriteUseCase(any()) }
    }

    @Test
    fun `LoadRecipe should set loading state during fetch`() = runTest {
        // Given
        val recipeId = 1
        coEvery { getRecipeDetailUseCase(recipeId) } coAnswers {
            kotlinx.coroutines.delay(100)
            Result.Success(testRecipeDetail)
        }
        coEvery { isFavoriteUseCase(recipeId) } returns false

        // When
        viewModel.state.test {
            awaitItem() // initial state
            
            viewModel.onIntent(DetailsIntent.LoadRecipe(recipeId))
            
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            testDispatcher.scheduler.advanceUntilIdle()
            
            val finalState = expectMostRecentItem()
            assertFalse(finalState.isLoading)
        }
    }

    @Test
    fun `ToggleFavorite should call useCase with correct favorite recipe`() = runTest {
        // Given
        val recipeId = 1
        coEvery { getRecipeDetailUseCase(recipeId) } returns Result.Success(testRecipeDetail)
        coEvery { isFavoriteUseCase(recipeId) } returns false
        
        val expectedFavorite = FavoriteRecipe(
            id = testRecipeDetail.id,
            title = testRecipeDetail.title,
            image = testRecipeDetail.image,
            readyInMinutes = testRecipeDetail.readyInMinutes,
            score = testRecipeDetail.spoonacularScore
        )
        coEvery { toggleFavoriteUseCase(expectedFavorite) } returns true

        viewModel.onIntent(DetailsIntent.LoadRecipe(recipeId))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onIntent(DetailsIntent.ToggleFavorite)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { toggleFavoriteUseCase(expectedFavorite) }
    }
}


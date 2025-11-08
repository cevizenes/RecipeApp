package org.example.recipeapp.presentation.home

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.example.recipeapp.core.util.Result
import org.example.recipeapp.domain.model.Recipe
import org.example.recipeapp.domain.usecase.GetRandomRecipesUseCase
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var getRandomRecipesUseCase: GetRandomRecipesUseCase
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testRecipes = listOf(
        Recipe(
            id = 1,
            title = "Recipe 1",
            image = "image1.jpg",
            readyInMinutes = 30,
            servings = 4,
            summary = "Summary 1",
            cuisines = emptyList(),
            dishTypes = emptyList(),
            diets = emptyList(),
            vegetarian = false,
            vegan = false,
            glutenFree = false,
            aggregateLikes = 100,
            spoonacularScore = 80.0
        ),
        Recipe(
            id = 2,
            title = "Recipe 2",
            image = "image2.jpg",
            readyInMinutes = 45,
            servings = 6,
            summary = "Summary 2",
            cuisines = emptyList(),
            dishTypes = emptyList(),
            diets = emptyList(),
            vegetarian = true,
            vegan = false,
            glutenFree = true,
            aggregateLikes = 150,
            spoonacularScore = 85.0
        )
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getRandomRecipesUseCase = mockk()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be empty and not loading`() {
        // Given
        coEvery { getRandomRecipesUseCase(15) } returns Result.Success(emptyList())

        // When
        viewModel = HomeViewModel(getRandomRecipesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state.featuredRecipes.isEmpty())
        assertTrue(state.popularRecipes.isEmpty())
    }

    @Test
    fun `init should load recipes automatically`() = runTest {
        // Given
        coEvery { getRandomRecipesUseCase(15) } returns Result.Success(testRecipes)

        // When
        viewModel = HomeViewModel(getRandomRecipesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { getRandomRecipesUseCase(15) }
    }

    @Test
    fun `LoadRecipes should update state with featured and popular recipes on success`() = runTest {
        // Given
        val recipes = List(15) { index ->
            testRecipes[0].copy(id = index, title = "Recipe $index")
        }
        coEvery { getRandomRecipesUseCase(15) } returns Result.Success(recipes)

        // When
        viewModel = HomeViewModel(getRandomRecipesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(1, state.featuredRecipes.size)
        assertEquals(14, state.popularRecipes.size)
        assertEquals("Recipe 0", state.featuredRecipes[0].title)
        assertEquals("Recipe 1", state.popularRecipes[0].title)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `LoadRecipes should set loading state during fetch`() = runTest {
        // Given - ViewModel'i oluştur (init'te otomatik load çağırır)
        coEvery { getRandomRecipesUseCase(15) } returns Result.Success(testRecipes)
        viewModel = HomeViewModel(getRandomRecipesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Initial load tamamlandı, state loading olmamalı
        val initialState = viewModel.state.value
        assertFalse(initialState.isLoading, "Initial state should not be loading after init")
        
        // Loading state'i test etmek için Retry intent'i kullan
        // UseCase'i delay ile çağırarak loading state'i gözlemleyebiliriz
        var useCaseCalled = false
        coEvery { getRandomRecipesUseCase(15) } coAnswers {
            useCaseCalled = true
            // UseCase çağrıldığında state loading olmalı
            val stateDuringCall = viewModel.state.value
            assertTrue(stateDuringCall.isLoading, "State should be loading when useCase is called")
            Result.Success(testRecipes)
        }
        
        // When - Retry intent'i çağır
        viewModel.onIntent(HomeIntent.Retry)
        
        // loadRecipes içinde state önce loading=true yapılır
        // Sonra useCase çağrılır, useCase içinde state'i kontrol ediyoruz
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - UseCase çağrıldı ve loading state kontrolü yapıldı
        assertTrue(useCaseCalled, "UseCase should be called")
        
        // Final state loading olmamalı
        val finalState = viewModel.state.value
        assertFalse(finalState.isLoading, "Final state should not be loading after completion")
    }

    @Test
    fun `LoadRecipes should update state with error on failure`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        val errorMessage = "Custom error message"
        coEvery { getRandomRecipesUseCase(15) } returns Result.Error(exception, errorMessage)

        // When
        viewModel = HomeViewModel(getRandomRecipesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
        assertTrue(state.featuredRecipes.isEmpty())
        assertTrue(state.popularRecipes.isEmpty())
    }

    @Test
    fun `LoadRecipes should emit ShowError effect on failure`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        val errorMessage = "Custom error message"
        coEvery { getRandomRecipesUseCase(15) } returns Result.Error(exception, errorMessage)

        // When
        viewModel = HomeViewModel(getRandomRecipesUseCase)

        // Then
        viewModel.effect.test {
            testDispatcher.scheduler.advanceUntilIdle()
            
            val effect = awaitItem()
            assertTrue(effect is HomeEffect.ShowError)
            assertEquals(errorMessage, (effect as HomeEffect.ShowError).message)
        }
    }

    @Test
    fun `Retry intent should reload recipes`() = runTest {
        // Given
        coEvery { getRandomRecipesUseCase(15) } returns Result.Success(testRecipes)

        viewModel = HomeViewModel(getRandomRecipesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onIntent(HomeIntent.Retry)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 2) { getRandomRecipesUseCase(15) }
    }

    @Test
    fun `error should use exception message when custom message is null`() = runTest {
        // Given
        val exception = RuntimeException("Exception message")
        coEvery { getRandomRecipesUseCase(15) } returns Result.Error(exception, null)

        // When
        viewModel = HomeViewModel(getRandomRecipesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals("Exception message", state.error)
    }

    @Test
    fun `error should use default message when both custom and exception messages are null`() = runTest {
        // Given
        val exception = RuntimeException()
        coEvery { getRandomRecipesUseCase(15) } returns Result.Error(exception, null)

        // When
        viewModel = HomeViewModel(getRandomRecipesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals("Unknown error", state.error)
    }
}


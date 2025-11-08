package org.example.recipeapp.presentation.search

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.example.recipeapp.core.util.Result
import org.example.recipeapp.domain.model.Recipe
import org.example.recipeapp.domain.usecase.SearchRecipesUseCase
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var searchRecipesUseCase: SearchRecipesUseCase
    private lateinit var viewModel: SearchViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testRecipe = Recipe(
        id = 1,
        title = "Pasta Carbonara",
        image = "image.jpg",
        readyInMinutes = 30,
        servings = 4,
        summary = "Delicious pasta",
        cuisines = listOf("Italian"),
        dishTypes = emptyList(),
        diets = emptyList(),
        vegetarian = false,
        vegan = false,
        glutenFree = false,
        aggregateLikes = 100,
        spoonacularScore = 80.0
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        searchRecipesUseCase = mockk()
        viewModel = SearchViewModel(searchRecipesUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be empty`() {
        // Then
        val state = viewModel.state.value
        assertEquals("", state.query)
        assertTrue(state.recipes.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertFalse(state.hasSearched)
    }

    @Test
    fun `QueryChanged should update query in state`() = runTest {
        coEvery {
            searchRecipesUseCase(query = any(), any(), any(), any(), any()) 
        } returns Result.Success(emptyList())

        // When
        viewModel.onIntent(SearchIntent.QueryChanged("pasta"))
        testDispatcher.scheduler.runCurrent()

        // Then
        assertEquals("pasta", viewModel.state.value.query)
    }

    @Test
    fun `QueryChanged should debounce search with 350ms delay`() = runTest {
        // Given
        coEvery { 
            searchRecipesUseCase(query = "pasta", null, null, null, null) 
        } returns Result.Success(listOf(testRecipe))

        // When
        viewModel.onIntent(SearchIntent.QueryChanged("pasta"))
        
        // Then - should not search immediately
        coVerify(exactly = 0) { searchRecipesUseCase(any(), any(), any(), any(), any()) }
        
        // Advance time past debounce delay
        testDispatcher.scheduler.advanceTimeBy(350)
        testDispatcher.scheduler.runCurrent()
        
        // Now search should be called
        coVerify(exactly = 1) { searchRecipesUseCase("pasta", null, null, null, null) }
    }

    @Test
    fun `QueryChanged with empty query should reset state`() = runTest {
        coEvery {
            searchRecipesUseCase(query = "pasta", null, null, null, null) 
        } returns Result.Success(listOf(testRecipe))
        
        viewModel.onIntent(SearchIntent.QueryChanged("pasta"))
        testDispatcher.scheduler.advanceTimeBy(350)
        testDispatcher.scheduler.runCurrent()

        // When - clear query
        viewModel.onIntent(SearchIntent.QueryChanged(""))

        // Then
        val state = viewModel.state.value
        assertEquals("", state.query)
        assertTrue(state.recipes.isEmpty())
        assertFalse(state.isLoading)
        assertFalse(state.hasSearched)
    }

    @Test
    fun `QueryChanged should cancel previous search job`() = runTest {
        // Given
        coEvery { 
            searchRecipesUseCase(query = any(), null, null, null, null) 
        } returns Result.Success(listOf(testRecipe))

        // When
        viewModel.onIntent(SearchIntent.QueryChanged("pas"))
        testDispatcher.scheduler.advanceTimeBy(200)
        
        viewModel.onIntent(SearchIntent.QueryChanged("pasta"))
        testDispatcher.scheduler.advanceTimeBy(350)
        testDispatcher.scheduler.runCurrent()

        coVerify(exactly = 0) { searchRecipesUseCase("pas", null, null, null, null) }
        coVerify(exactly = 1) { searchRecipesUseCase("pasta", null, null, null, null) }
    }

    @Test
    fun `Search intent should perform search immediately`() = runTest {
        coEvery {
            searchRecipesUseCase(query = "pasta", null, null, null, null) 
        } returns Result.Success(listOf(testRecipe))
        
        viewModel.onIntent(SearchIntent.QueryChanged("pasta"))
        testDispatcher.scheduler.runCurrent()
        
        testDispatcher.scheduler.advanceTimeBy(100)

        viewModel.onIntent(SearchIntent.Search)
        
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(atLeast = 1) { searchRecipesUseCase("pasta", null, null, null, null) }
        
        val state = viewModel.state.value
        assertEquals(1, state.recipes.size)
        assertFalse(state.isLoading)
        assertTrue(state.hasSearched)
    }

    @Test
    fun `QuickSearch should update query and search immediately`() = runTest {
        // Given
        val quickQuery = "chicken"
        coEvery { 
            searchRecipesUseCase(query = quickQuery, null, null, null, null) 
        } returns Result.Success(listOf(testRecipe))

        // When
        viewModel.onIntent(SearchIntent.QuickSearch(quickQuery))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(quickQuery, viewModel.state.value.query)
        coVerify(exactly = 1) { searchRecipesUseCase(quickQuery, null, null, null, null) }
    }

    @Test
    fun `SearchByType should search by type with null query`() = runTest {
        // Given
        val type = "dessert"
        coEvery { 
            searchRecipesUseCase(query = null, null, null, type, null) 
        } returns Result.Success(listOf(testRecipe))

        // When
        viewModel.onIntent(SearchIntent.SearchByType(type))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { searchRecipesUseCase(null, null, null, type, null) }
    }

    @Test
    fun `ClearSearch should reset state`() = runTest {
        // Given - perform a search first
        coEvery { 
            searchRecipesUseCase(query = "pasta", null, null, null, null) 
        } returns Result.Success(listOf(testRecipe))
        
        viewModel.onIntent(SearchIntent.QueryChanged("pasta"))
        testDispatcher.scheduler.advanceTimeBy(350)
        testDispatcher.scheduler.runCurrent()

        // When
        viewModel.onIntent(SearchIntent.ClearSearch)

        // Then
        val state = viewModel.state.value
        assertEquals("", state.query)
        assertTrue(state.recipes.isEmpty())
        assertFalse(state.isLoading)
        assertFalse(state.hasSearched)
    }

    @Test
    fun `successful search should update state with results`() = runTest {
        // Given
        val recipes = listOf(testRecipe)
        coEvery { 
            searchRecipesUseCase(query = "pasta", null, null, null, null) 
        } returns Result.Success(recipes)

        // When
        viewModel.onIntent(SearchIntent.QueryChanged("pasta"))
        testDispatcher.scheduler.advanceTimeBy(350)
        testDispatcher.scheduler.runCurrent()

        // Then
        val state = viewModel.state.value
        assertEquals(recipes, state.recipes)
        assertFalse(state.isLoading)
        assertTrue(state.hasSearched)
        assertNull(state.error)
    }

    @Test
    fun `failed search should update state with error`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        val errorMessage = "Search failed"
        coEvery { 
            searchRecipesUseCase(query = "pasta", null, null, null, null) 
        } returns Result.Error(exception, errorMessage)

        // When
        viewModel.onIntent(SearchIntent.QueryChanged("pasta"))
        testDispatcher.scheduler.advanceTimeBy(350)
        testDispatcher.scheduler.runCurrent()

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `failed search should emit ShowError effect`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        val errorMessage = "Search failed"
        coEvery { 
            searchRecipesUseCase(query = "pasta", null, null, null, null) 
        } returns Result.Error(exception, errorMessage)

        // When
        viewModel.effect.test {
            viewModel.onIntent(SearchIntent.QueryChanged("pasta"))
            testDispatcher.scheduler.advanceTimeBy(350)
            testDispatcher.scheduler.runCurrent()

            // Then
            val effect = awaitItem()
            assertTrue(effect is SearchEffect.ShowError)
            assertEquals(errorMessage, (effect as SearchEffect.ShowError).message)
        }
    }

    @Test
    fun `performSearch should not execute with blank query and no type`() = runTest {
        // Given
        coEvery { 
            searchRecipesUseCase(any(), any(), any(), any(), any()) 
        } returns Result.Success(emptyList())

        // When
        viewModel.onIntent(SearchIntent.QueryChanged("   "))
        testDispatcher.scheduler.advanceTimeBy(350)
        testDispatcher.scheduler.runCurrent()

        // Then
        coVerify(exactly = 0) { searchRecipesUseCase(any(), any(), any(), any(), any()) }
    }
}


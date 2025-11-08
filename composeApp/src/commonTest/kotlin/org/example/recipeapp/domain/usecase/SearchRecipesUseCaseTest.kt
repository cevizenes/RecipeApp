package org.example.recipeapp.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.recipeapp.core.util.Result
import org.example.recipeapp.domain.model.Recipe
import org.example.recipeapp.domain.repository.RecipeRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchRecipesUseCaseTest {

    private val repository = mockk<RecipeRepository>()
    private val useCase = SearchRecipesUseCase(repository)

    private val testRecipe = Recipe(
        id = 1,
        title = "Test Recipe",
        image = "image.jpg",
        readyInMinutes = 30,
        servings = 4,
        summary = "Test summary",
        cuisines = emptyList(),
        dishTypes = emptyList(),
        diets = emptyList(),
        vegetarian = false,
        vegan = false,
        glutenFree = false,
        aggregateLikes = 100,
        spoonacularScore = 80.0
    )

    @Test
    fun `invoke should return success when query is provided`() = runTest {
        // Given
        val query = "pasta"
        val expectedRecipes = listOf(testRecipe)
        coEvery { 
            repository.searchRecipes(query, null, null, null, null) 
        } returns Result.Success(expectedRecipes)

        // When
        val result = useCase.invoke(query)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedRecipes, result.data)
        coVerify(exactly = 1) { repository.searchRecipes(query, null, null, null, null) }
    }

    @Test
    fun `invoke should return error when query is blank and type is null`() = runTest {
        // Given
        val query = "   "

        // When
        val result = useCase.invoke(query)

        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is IllegalArgumentException)
        coVerify(exactly = 0) { repository.searchRecipes(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `invoke should return error when query is null and type is null`() = runTest {
        // When
        val result = useCase.invoke(null)

        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is IllegalArgumentException)
        coVerify(exactly = 0) { repository.searchRecipes(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `invoke should succeed when query is null but type is provided`() = runTest {
        // Given
        val type = "dessert"
        val expectedRecipes = listOf(testRecipe)
        coEvery { 
            repository.searchRecipes("", null, null, type, null) 
        } returns Result.Success(expectedRecipes)

        // When
        val result = useCase.invoke(null, type = type)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedRecipes, result.data)
        coVerify(exactly = 1) { repository.searchRecipes("", null, null, type, null) }
    }

    @Test
    fun `invoke should pass all parameters to repository`() = runTest {
        // Given
        val query = "pasta"
        val cuisine = "Italian"
        val diet = "vegetarian"
        val type = "main course"
        val maxReadyTime = 30
        val expectedRecipes = listOf(testRecipe)
        
        coEvery { 
            repository.searchRecipes(query, cuisine, diet, type, maxReadyTime) 
        } returns Result.Success(expectedRecipes)

        // When
        val result = useCase.invoke(query, cuisine, diet, type, maxReadyTime)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedRecipes, result.data)
        coVerify(exactly = 1) { 
            repository.searchRecipes(query, cuisine, diet, type, maxReadyTime) 
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        val query = "pasta"
        val exception = RuntimeException("Network error")
        coEvery { 
            repository.searchRecipes(query, null, null, null, null) 
        } returns Result.Error(exception, "Error message")

        // When
        val result = useCase.invoke(query)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, result.exception)
        coVerify(exactly = 1) { repository.searchRecipes(query, null, null, null, null) }
    }
}


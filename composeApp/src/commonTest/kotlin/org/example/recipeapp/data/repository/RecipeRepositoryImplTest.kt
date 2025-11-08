package org.example.recipeapp.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.recipeapp.core.util.Result
import org.example.recipeapp.data.remote.api.SpoonacularApi
import org.example.recipeapp.data.remote.dto.RecipeDetailDto
import org.example.recipeapp.data.remote.dto.RecipeDto
import org.example.recipeapp.data.remote.dto.SearchRecipesResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RecipeRepositoryImplTest {

    private val api = mockk<SpoonacularApi>()
    private val repository = RecipeRepositoryImpl(api)

    private val testRecipeDto = RecipeDto(
        id = 1,
        title = "Test Recipe",
        image = "image.jpg",
        readyInMinutes = 30,
        servings = 4,
        summary = "Test summary",
        cuisines = listOf("Italian"),
        dishTypes = listOf("main course"),
        diets = emptyList(),
        vegetarian = false,
        vegan = false,
        glutenFree = false,
        aggregateLikes = 100,
        spoonacularScore = 80.0
    )

    private val testRecipeDetailDto = RecipeDetailDto(
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
        extendedIngredients = emptyList(),
        analyzedInstructions = emptyList(),
        nutrition = null,
        vegetarian = false,
        vegan = false,
        glutenFree = false,
        aggregateLikes = 100,
        spoonacularScore = 80.0
    )

    @Test
    fun `searchRecipes should return success when api call succeeds`() = runTest {
        // Given
        val query = "pasta"
        val response = SearchRecipesResponse(
            results = listOf(testRecipeDto),
            offset = 0,
            number = 20,
            totalResults = 1
        )
        coEvery { 
            api.searchRecipes(query, any(), any(), null, null, null, null) 
        } returns response

        // When
        val result = repository.searchRecipes(query)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(1, result.data.size)
        assertEquals("Test Recipe", result.data[0].title)
        coVerify(exactly = 1) { 
            api.searchRecipes(query, any(), any(), null, null, null, null) 
        }
    }

    @Test
    fun `searchRecipes should return error when api call fails`() = runTest {
        // Given
        val query = "pasta"
        val exception = RuntimeException("Network error")
        coEvery { 
            api.searchRecipes(query, any(), any(), null, null, null, null) 
        } throws exception

        // When
        val result = repository.searchRecipes(query)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, result.exception)
        coVerify(exactly = 1) { 
            api.searchRecipes(query, any(), any(), null, null, null, null) 
        }
    }

    @Test
    fun `searchRecipes should pass all parameters to api`() = runTest {
        // Given
        val query = "pasta"
        val cuisine = "Italian"
        val diet = "vegetarian"
        val type = "main course"
        val maxReadyTime = 30
        val response = SearchRecipesResponse(
            results = emptyList(),
            offset = 0,
            number = 20,
            totalResults = 0
        )
        coEvery { 
            api.searchRecipes(query, any(), any(), cuisine, diet, type, maxReadyTime) 
        } returns response

        // When
        repository.searchRecipes(query, cuisine, diet, type, maxReadyTime)

        // Then
        coVerify(exactly = 1) { 
            api.searchRecipes(query, any(), any(), cuisine, diet, type, maxReadyTime) 
        }
    }

    @Test
    fun `getRecipeDetail should return success when api call succeeds`() = runTest {
        // Given
        val recipeId = 1
        coEvery { api.getRecipeDetail(recipeId) } returns testRecipeDetailDto

        // When
        val result = repository.getRecipeDetail(recipeId)

        // Then
        assertTrue(result is Result.Success)
        assertEquals("Test Recipe Detail", result.data.title)
        assertEquals(recipeId, result.data.id)
        coVerify(exactly = 1) { api.getRecipeDetail(recipeId) }
    }

    @Test
    fun `getRecipeDetail should return error when api call fails`() = runTest {
        // Given
        val recipeId = 1
        val exception = RuntimeException("Recipe not found")
        coEvery { api.getRecipeDetail(recipeId) } throws exception

        // When
        val result = repository.getRecipeDetail(recipeId)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, result.exception)
        coVerify(exactly = 1) { api.getRecipeDetail(recipeId) }
    }

    @Test
    fun `getRandomRecipes should return success when api call succeeds`() = runTest {
        // Given
        val count = 10
        val response = SearchRecipesResponse(
            results = listOf(testRecipeDto),
            offset = 0,
            number = count,
            totalResults = 1
        )
        coEvery { api.getRandomRecipes(count) } returns response

        // When
        val result = repository.getRandomRecipes(count)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(1, result.data.size)
        assertEquals("Test Recipe", result.data[0].title)
        coVerify(exactly = 1) { api.getRandomRecipes(count) }
    }

    @Test
    fun `getRandomRecipes should return error when api call fails`() = runTest {
        // Given
        val count = 10
        val exception = RuntimeException("Network error")
        coEvery { api.getRandomRecipes(count) } throws exception

        // When
        val result = repository.getRandomRecipes(count)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, result.exception)
        coVerify(exactly = 1) { api.getRandomRecipes(count) }
    }

    @Test
    fun `getRandomRecipes should map multiple recipes correctly`() = runTest {
        // Given
        val count = 3
        val recipes = listOf(
            testRecipeDto.copy(id = 1, title = "Recipe 1"),
            testRecipeDto.copy(id = 2, title = "Recipe 2"),
            testRecipeDto.copy(id = 3, title = "Recipe 3")
        )
        val response = SearchRecipesResponse(
            results = recipes,
            offset = 0,
            number = count,
            totalResults = 3
        )
        coEvery { api.getRandomRecipes(count) } returns response

        // When
        val result = repository.getRandomRecipes(count)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(3, result.data.size)
        assertEquals("Recipe 1", result.data[0].title)
        assertEquals("Recipe 2", result.data[1].title)
        assertEquals("Recipe 3", result.data[2].title)
    }
}


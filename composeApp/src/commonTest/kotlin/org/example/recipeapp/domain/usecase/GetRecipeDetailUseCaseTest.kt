package org.example.recipeapp.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.recipeapp.core.util.Result
import org.example.recipeapp.domain.model.RecipeDetail
import org.example.recipeapp.domain.repository.RecipeRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetRecipeDetailUseCaseTest {

    private val repository = mockk<RecipeRepository>()
    private val useCase = GetRecipeDetailUseCase(repository)

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

    @Test
    fun `invoke should return success when repository returns success`() = runTest {
        // Given
        val recipeId = 1
        coEvery { repository.getRecipeDetail(recipeId) } returns Result.Success(testRecipeDetail)

        // When
        val result = useCase.invoke(recipeId)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(testRecipeDetail, result.data)
        coVerify(exactly = 1) { repository.getRecipeDetail(recipeId) }
    }

    @Test
    fun `invoke should return error when repository returns error`() = runTest {
        // Given
        val recipeId = 1
        val exception = RuntimeException("Recipe not found")
        coEvery { repository.getRecipeDetail(recipeId) } returns Result.Error(exception, "Error message")

        // When
        val result = useCase.invoke(recipeId)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, result.exception)
        coVerify(exactly = 1) { repository.getRecipeDetail(recipeId) }
    }

    @Test
    fun `invoke should pass correct id to repository`() = runTest {
        // Given
        val recipeId = 123
        coEvery { repository.getRecipeDetail(recipeId) } returns Result.Success(testRecipeDetail)

        // When
        useCase.invoke(recipeId)

        // Then
        coVerify(exactly = 1) { repository.getRecipeDetail(recipeId) }
    }
}


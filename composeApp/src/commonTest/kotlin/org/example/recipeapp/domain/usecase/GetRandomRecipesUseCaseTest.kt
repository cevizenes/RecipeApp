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

class GetRandomRecipesUseCaseTest {

    private val repository = mockk<RecipeRepository>()
    private val useCase = GetRandomRecipesUseCase(repository)

    @Test
    fun `invoke should return success when repository returns success`() = runTest {
        // Given
        val expectedRecipes = listOf(
            Recipe(
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
        )
        coEvery { repository.getRandomRecipes(10) } returns Result.Success(expectedRecipes)

        // When
        val result = useCase.invoke(10)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedRecipes, result.data)
        coVerify(exactly = 1) { repository.getRandomRecipes(10) }
    }

    @Test
    fun `invoke should return error when repository returns error`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        coEvery { repository.getRandomRecipes(10) } returns Result.Error(exception, "Error message")

        // When
        val result = useCase.invoke(10)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, result.exception)
        coVerify(exactly = 1) { repository.getRandomRecipes(10) }
    }

    @Test
    fun `invoke should use default count when not provided`() = runTest {
        // Given
        val expectedRecipes = emptyList<Recipe>()
        coEvery { repository.getRandomRecipes(10) } returns Result.Success(expectedRecipes)

        // When
        val result = useCase.invoke()

        // Then
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { repository.getRandomRecipes(10) }
    }

    @Test
    fun `invoke should pass custom count to repository`() = runTest {
        // Given
        val customCount = 5
        val expectedRecipes = emptyList<Recipe>()
        coEvery { repository.getRandomRecipes(customCount) } returns Result.Success(expectedRecipes)

        // When
        val result = useCase.invoke(customCount)

        // Then
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { repository.getRandomRecipes(customCount) }
    }
}


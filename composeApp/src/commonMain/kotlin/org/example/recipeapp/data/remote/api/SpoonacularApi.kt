package org.example.recipeapp.data.remote.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.example.recipeapp.data.remote.dto.RecipeDetailDto
import org.example.recipeapp.data.remote.dto.SearchRecipesResponse

class SpoonacularApi(
    private val client: HttpClient,
    private val apiKey: String
) {
    companion object {
        private const val BASE_URL = "https://api.spoonacular.com"
    }

    suspend fun searchRecipes(
        query: String,
        number: Int = 20,
        offset: Int = 0,
        cuisine: String? = null,
        diet: String? = null,
        type: String? = null,
        maxReadyTime: Int? = null
    ): SearchRecipesResponse {
        return client.get("$BASE_URL/recipes/complexSearch") {
            parameter("apiKey", apiKey)
            parameter("query", query)
            parameter("number", number)
            parameter("offset", offset)
            parameter("addRecipeInformation", true)
            parameter("fillIngredients", true)
            cuisine?.let { parameter("cuisine", it) }
            diet?.let { parameter("diet", it) }
            type?.let { parameter("type", it) }
            maxReadyTime?.let { parameter("maxReadyTime", it) }
        }.body()
    }

    suspend fun getRecipeDetail(id: Int): RecipeDetailDto {
        return client.get("$BASE_URL/recipes/$id/information") {
            parameter("apiKey", apiKey)
            parameter("includeNutrition", true)
        }.body()
    }

    suspend fun getRandomRecipes(number: Int = 10): SearchRecipesResponse {
        return client.get("$BASE_URL/recipes/random") {
            parameter("apiKey", apiKey)
            parameter("number", number)
        }.body<Map<String, List<RecipeDetailDto>>>().let {
            SearchRecipesResponse(
                results = it["recipes"]?.map { dto ->
                    org.example.recipeapp.data.remote.dto.RecipeDto(
                        id = dto.id,
                        title = dto.title,
                        image = dto.image,
                        readyInMinutes = dto.readyInMinutes,
                        servings = dto.servings,
                        summary = dto.summary,
                        cuisines = dto.cuisines,
                        dishTypes = dto.dishTypes,
                        diets = dto.diets,
                        vegetarian = dto.vegetarian,
                        vegan = dto.vegan,
                        glutenFree = dto.glutenFree,
                        aggregateLikes = dto.aggregateLikes,
                        spoonacularScore = dto.spoonacularScore
                    )
                } ?: emptyList(),
                offset = 0,
                number = number,
                totalResults = number
            )
        }
    }
}
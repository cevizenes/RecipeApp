package org.example.recipeapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchRecipesResponse(
    val results: List<RecipeDto>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
)

@Serializable
data class RecipeDto(
    val id: Int,
    val title: String,
    val image: String? = null,
    val imageType: String? = null,
    @SerialName("readyInMinutes") val readyInMinutes: Int? = null,
    val servings: Int? = null,
    val summary: String? = null,
    val cuisines: List<String> = emptyList(),
    val dishTypes: List<String> = emptyList(),
    val diets: List<String> = emptyList(),
    val vegetarian: Boolean = false,
    val vegan: Boolean = false,
    val glutenFree: Boolean = false,
    val dairyFree: Boolean = false,
    val veryHealthy: Boolean = false,
    val cheap: Boolean = false,
    val veryPopular: Boolean = false,
    val sustainable: Boolean = false,
    val pricePerServing: Double? = null,
    val aggregateLikes: Int? = null,
    val spoonacularScore: Double? = null,
    val healthScore: Double? = null
)

@Serializable
data class RecipeDetailDto(
    val id: Int,
    val title: String,
    val image: String? = null,
    @SerialName("readyInMinutes") val readyInMinutes: Int? = null,
    val servings: Int? = null,
    val summary: String? = null,
    val cuisines: List<String> = emptyList(),
    val dishTypes: List<String> = emptyList(),
    val diets: List<String> = emptyList(),
    val instructions: String? = null,
    val extendedIngredients: List<IngredientDto> = emptyList(),
    val analyzedInstructions: List<InstructionDto> = emptyList(),
    val nutrition: NutritionDto? = null,
    val vegetarian: Boolean = false,
    val vegan: Boolean = false,
    val glutenFree: Boolean = false,
    val aggregateLikes: Int? = null,
    val spoonacularScore: Double? = null
)

@Serializable
data class IngredientDto(
    val id: Int,
    val name: String,
    val original: String,
    val amount: Double,
    val unit: String,
    val image: String? = null
)

@Serializable
data class InstructionDto(
    val name: String = "",
    val steps: List<StepDto> = emptyList()
)

@Serializable
data class StepDto(
    val number: Int,
    val step: String,
    val ingredients: List<StepIngredientDto> = emptyList(),
    val equipment: List<StepEquipmentDto> = emptyList()
)

@Serializable
data class StepIngredientDto(
    val id: Int,
    val name: String,
    val image: String? = null
)

@Serializable
data class StepEquipmentDto(
    val id: Int,
    val name: String,
    val image: String? = null
)

@Serializable
data class NutritionDto(
    val nutrients: List<NutrientDto> = emptyList(),
    val caloricBreakdown: CaloricBreakdownDto? = null
)

@Serializable
data class NutrientDto(
    val name: String,
    val amount: Double,
    val unit: String,
    val percentOfDailyNeeds: Double? = null
)

@Serializable
data class CaloricBreakdownDto(
    val percentProtein: Double,
    val percentFat: Double,
    val percentCarbs: Double
)
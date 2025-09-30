package org.example.recipeapp.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.example.recipeapp.core.network.HttpClientFactory
import org.example.recipeapp.data.remote.api.SpoonacularApi
import org.example.recipeapp.data.repository.RecipeRepositoryImpl
import org.example.recipeapp.domain.repository.RecipeRepository
import org.example.recipeapp.domain.usecase.GetRandomRecipesUseCase
import org.example.recipeapp.domain.usecase.GetRecipeDetailUseCase
import org.example.recipeapp.domain.usecase.SearchRecipesUseCase
import org.example.recipeapp.presentation.details.DetailsViewModel
import org.example.recipeapp.presentation.home.HomeViewModel
import org.example.recipeapp.presentation.search.SearchViewModel

val networkModule = module {
    single { HttpClientFactory().create() }
    single {
        SpoonacularApi(
            client = get(),
            apiKey = getProperty("SPOONACULAR_API_KEY", "YOUR_API_KEY_HERE")
        )
    }
}

val repositoryModule = module {
    single<RecipeRepository> { RecipeRepositoryImpl(get()) }
}

val useCaseModule = module {
    singleOf(::SearchRecipesUseCase)
    singleOf(::GetRecipeDetailUseCase)
    singleOf(::GetRandomRecipesUseCase)
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::DetailsViewModel)
}

val appModules: List<Module> = listOf(
    networkModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)
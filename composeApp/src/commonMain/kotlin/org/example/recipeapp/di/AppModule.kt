package org.example.recipeapp.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.example.recipeapp.core.network.createHttpClient
import org.example.recipeapp.data.remote.api.SpoonacularApi
import org.example.recipeapp.data.repository.FavoritesRepositoryImpl
import org.example.recipeapp.data.repository.RecipeRepositoryImpl
import org.example.recipeapp.db.AppDatabase
import org.example.recipeapp.db.createDatabase
import org.example.recipeapp.domain.repository.FavoritesRepository
import org.example.recipeapp.domain.repository.RecipeRepository
import org.example.recipeapp.domain.usecase.AddFavoriteUseCase
import org.example.recipeapp.domain.usecase.GetRandomRecipesUseCase
import org.example.recipeapp.domain.usecase.GetRecipeDetailUseCase
import org.example.recipeapp.domain.usecase.IsFavoriteUseCase
import org.example.recipeapp.domain.usecase.ObserveFavoritesUseCase
import org.example.recipeapp.domain.usecase.RemoveFavoriteUseCase
import org.example.recipeapp.domain.usecase.SearchRecipesUseCase
import org.example.recipeapp.domain.usecase.ToggleFavoriteUseCase
import org.example.recipeapp.presentation.details.DetailsViewModel
import org.example.recipeapp.presentation.favorites.FavoritesViewModel
import org.example.recipeapp.presentation.home.HomeViewModel
import org.example.recipeapp.presentation.search.SearchViewModel

val networkModule = module {
    single { createHttpClient() }
    single {
        SpoonacularApi(
            client = get(),
            apiKey = getProperty("SPOONACULAR_API_KEY", "43150cf46b1646abb903bc2fb99ca714")
        )
    }
}

val repositoryModule = module {
    single<RecipeRepository> { RecipeRepositoryImpl(get()) }
    single<FavoritesRepository> { FavoritesRepositoryImpl(get()) }
}

val useCaseModule = module {
    singleOf(::SearchRecipesUseCase)
    singleOf(::GetRecipeDetailUseCase)
    singleOf(::GetRandomRecipesUseCase)
    singleOf(::AddFavoriteUseCase)
    singleOf(::IsFavoriteUseCase)
    singleOf(::ObserveFavoritesUseCase)
    singleOf(::RemoveFavoriteUseCase)
    singleOf(::ToggleFavoriteUseCase)
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::DetailsViewModel)
    viewModelOf(::FavoritesViewModel)
}

val databaseModule = module {
    single<AppDatabase> { createDatabase(get()) }
    single<FavoritesRepository> { FavoritesRepositoryImpl(get()) }
}

val appModules: List<Module> = listOf(
    networkModule,
    repositoryModule,
    useCaseModule,
    viewModelModule,
    databaseModule
)
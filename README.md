# RecipeApp

RecipeApp is a Kotlin Multiplatform app that showcases recipes from the Spoonacular API using a shared UI with Compose Multiplatform for Android and iOS.

## Features
- Home with featured and popular recipes
- Search by text (debounced) and quick chips
- Recipe details
- Favorites with local persistence
- Snackbar-based error handling
- Image loading with placeholders and loading indicators
- Dark Mode support on both Android and iOS

## Tech Stack
- Kotlin Multiplatform (KMP)
- Compose Multiplatform
- Voyager (Navigation)
- Koin (Dependency Injection)
- Ktor (Networking)
- Coil 3 (Images)
- SQLDelight (Local storage)
- Coroutines + StateFlow/Flow

## Architecture
- MVVM + Unidirectional Data Flow
- Contracts per screen: State, Intent, Effect
- Shared domain/data layers across platforms

## Project Structure
- `composeApp/src/commonMain/` — Shared UI, ViewModels, use cases, repository
- `composeApp/src/androidMain/` — Android-specific
- `composeApp/src/iosMain/` — iOS-specific
- `iosApp/` — iOS launcher project
- `build.gradle.kts`, `settings.gradle.kts` — Build configuration
- 
## Spoonacular API Key (Quick Setup)

Get your Spoonacular API key and paste it directly in the DI module for local testing.

Edit `composeApp/src/commonMain/kotlin/org/example/recipeapp/di/AppModule.kt`:

```kotlin
// AppModule.kt
val networkModule = module {
    single { createHttpClient() }
    single {
        SpoonacularApi(
            client = get(),
            apiKey = "YOUR_SPOONACULAR_API_KEY_HERE" // <-- put your key here for local runs
        )
    }
}
```

## Screenshots

### Android
<table>
  <tr>
    <td><img src="screenshots/Home_Tab_Android.png" alt="Android Home" width="300"/></td>
    <td><img src="screenshots/Search_Tab_Android.png" alt="Android Search" width="300"/></td>
    <td><img src="screenshots/Favorites_Tab_Android.png" alt="Android Favorites" width="300"/></td>
    <td><img src="screenshots/Details_Screen_Android.png" alt="Android Details" width="300"/></td>
  </tr>
  <tr>
    <td align="center"><b>Home</b></td>
    <td align="center"><b>Search</b></td>
    <td align="center"><b>Favorites</b></td>
    <td align="center"><b>Details</b></td>
  </tr>
</table>

### iOS
<table>
  <tr>
    <td><img src="screenshots/Home_Tab_Ios.png" alt="iOS Home" width="300"/></td>
    <td><img src="screenshots/Search_Tab_Ios.png" alt="iOS Search" width="300"/></td>
    <td><img src="screenshots/Favorites_Tab_Ios.png" alt="iOS Favorites" width="300"/></td>
    <td><img src="screenshots/Details_Screen_Ios.png" alt="iOS Details" width="300"/></td>
  </tr>
  <tr>
    <td align="center"><b>Home</b></td>
    <td align="center"><b>Search</b></td>
    <td align="center"><b>Favorites</b></td>
    <td align="center"><b>Details</b></td>
  </tr>
</table>

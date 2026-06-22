# SmartCart Merchant

SmartCart Merchant is the Android application for merchants using the SmartCart platform. It provides the tools needed to register, verify a business, set up store branches, and manage daily operations through an intuitive dashboard.

## Features

- Merchant registration and sign-in
- RUC (business tax ID) verification
- Branch registration with map location and opening hours
- Main dashboard with side navigation optimized for tablets
- Session management with secure token storage

## Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose with Material 3
- **Architecture:** MVVM + Clean Architecture per feature
- **Dependency Injection:** Hilt
- **Networking:** Retrofit, Gson, OkHttp
- **Navigation:** Navigation Compose with type-safe routes
- **Local Storage:** DataStore Preferences for session, Room prepared for future use
- **Maps:** Google Maps Compose

## Architecture

The project is organized by feature, with each feature containing its own data, domain, presentation, and dependency injection layers:

- **Presentation:** Composables, ViewModels, and UiState
- **Domain:** Models, repository interfaces, and use cases
- **Data:** Retrofit APIs, DTOs, and repository implementations

Shared code lives in the `core` package, and top-level navigation is handled in `AppNavigation.kt`.

## App Flow

```
Splash
  ├─ Session exists and verified → Dashboard
  ├─ Session exists but not verified → Verification
  └─ No session → Auth (Sign In / Sign Up)

Auth → Verification
Verification → Store Setup
Store Setup → Dashboard
```

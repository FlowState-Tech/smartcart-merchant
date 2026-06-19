# SmartCart Merchant — Contexto para Agentes de IA

Este documento describe la arquitectura, convenciones y flujos clave del proyecto para que cualquier agente de codificación pueda trabajar de manera consistente.

## 1. Visión general

SmartCart Merchant es la aplicación Android para comerciantes de SmartCart. Permite:

- Registro e inicio de sesión de comerciantes.
- Verificación del RUC de la empresa.
- Registro de sucursales con ubicación en mapa y horarios de atención.
- Acceso a un dashboard principal con navegación lateral (NavigationRail) optimizado para tabletas.

## 2. Stack tecnológico

- **Lenguaje**: Kotlin 2.3.21
- **Framework UI**: Jetpack Compose (Material 3)
- **Arquitectura**: MVVM + Clean Architecture por feature
- **Inyección de dependencias**: Hilt 2.59.2
- **Red**: Retrofit 3.0.0 + Gson + OkHttp Logging Interceptor
- **Navegación**: Navigation Compose con Kotlin Serialization para rutas type-safe
- **Persistencia local**: DataStore Preferences (sesión) y Room (preparado pero aún no usado intensivamente)
- **Imágenes**: Coil 3
- **Mapas**: Google Maps Compose + Play Services Maps
- **Build**: Gradle con Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml`)

## 3. Estructura del proyecto

```
app/src/main/java/com/smartcart_merchant/
├── core/                       # Código compartido entre features
│   ├── common/                 # Resource<T>, utilidades
│   ├── navigation/             # RootDestination (rutas raíz type-safe)
│   ├── network/                # AuthInterceptor, configuración de Retrofit
│   └── storage/                # SessionPreferences (DataStore)
├── di/
│   └── AppModule.kt            # Provee Retrofit, OkHttp, SessionPreferences y APIs globales
├── features/                   # Cada feature es autónomo
│   ├── auth/                   # Login y registro
│   ├── verification/           # Verificación de RUC
│   ├── store/                  # Registro de sucursales
│   │   ├── data/
│   │   ├── domain/
│   │   ├── presentation/
│   │   └── di/
│   └── dashboard/              # Dashboard principal con NavigationRail
│       ├── domain/             # Modelos (DashboardDestination)
│       └── presentation/       # ViewModel, UiState, Screen, componentes y secciones
├── ui/
│   ├── navigation/
│   │   └── AppNavigation.kt    # Navegación top-level (splash, auth, verification, store, dashboard)
│   ├── screens/
│   │   ├── AppScreens.kt       # SplashScreen y MainDashboardScreen (wrapper del dashboard)
│   └── theme/                  # Colores, tipografía, tema
└── MainActivity.kt
```

Cada feature sigue la misma estructura de capas:

```
feature/
├── data/
│   ├── remote/
│   │   ├── api/
│   │   └── dto/
│   └── repository/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
└── presentation/
    ├── navigation/
    ├── state/
    ├── ui/screens/
    └── viewmodel/
```

## 4. Arquitectura

### 4.1 Capas

- **Presentation**: Composables + ViewModel + UiState. Los Composables reaccionan a `StateFlow<UiState>`. Los eventos de UI llaman métodos del ViewModel.
- **Domain**: Modelos de dominio, interfaces de repositorio y casos de uso. No depende de Android ni de frameworks externos.
- **Data**: Implementaciones de repositorios, APIs Retrofit y DTOs. Realiza el mapeo DTO -> dominio.

### 4.2 UiState

Cada pantalla expone un data class `UiState` con:

- Campos de formulario o datos a mostrar.
- `isLoading: Boolean`.
- `error: String?` o `errorMessage: String?`.
- `isSuccess: Boolean` para eventos de navegación.

Ejemplo:

```kotlin
data class StoreUiState(
    val merchantId: String = "",
    val ruc: String = "",
    val storeName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
```

### 4.3 Navegación

La app usa dos niveles de navegación:

1. **Top-level (`AppNavigation.kt`)**: controlado por un `mutableStateOf<AppScreen>`. Decidir qué flujo mostrar (splash, auth, verification, store setup, dashboard).
2. **Feature-level (`AuthNavGraph`, `StoreNavGraph`)**: `NavHost` interno para flujos con varias pantallas dentro de un mismo feature.

Reglas:

- El `AppNavigation` decide el destino inicial leyendo `SessionPreferences`.
- Cuando una operación termina con éxito (login, verificación, creación de tienda), el callback correspondiente cambia el `currentScreen` de `AppNavigation`.
- Para eventos de éxito de un solo uso, usar `LaunchedEffect(uiState.isSuccess)` y resetear `isSuccess` en el ViewModel (`onStoreCreated()`, etc.).

## 5. Flujo de la aplicación

```
Splash
  └─ Si hay sesión y está verificado -> Dashboard
  └─ Si hay sesión y no está verificado -> Verification
  └─ Si no hay sesión -> Auth (SignIn / SignUp)

Auth
  └─ Login exitoso -> Verification o Dashboard según isVerified

Verification
  └─ RUC verificado -> Store Setup

Store Setup
  └─ Sucursal creada -> Dashboard
```

## 6. Convenciones de código

### 6.1 Nombres

- Paquetes: `com.smartcart_merchant.features.<feature>.<capa>.<subcapa>`
- ViewModels: `<Feature>ViewModel`
- Screens: `<Feature>Screen`
- UiState: `<Feature>UiState`
- Use cases: `<Action><Entity>UseCase`
- Repositorios: `<Entity>Repository` / `<Entity>RepositoryImpl`

### 6.2 Composables

- Los Composables reciben lambdas para navegación (`onLoginSuccess`, `onStoreSuccess`, `onNavigateToSignUp`).
- No llamar a navegación directamente desde el ViewModel; el ViewModel actualiza el estado y la UI reacciona.
- Usar `collectAsState()` para observar `StateFlow`.
- Mostrar errores con `SnackbarHost` mediante `LaunchedEffect(uiState.error)`.

### 6.3 Repositorios

- Devolver `Resource<T>`: `Success`, `Error`, `Loading`.
- Capturar excepciones y convertirlas en `Resource.Error`.
- Usar `Log.d` / `Log.e` para depuración de red.

### 6.4 DTOs

- Usar `@SerializedName` para mapeo Gson.
- Incluir `toDomain()` para convertir DTO -> modelo de dominio.
- Incluir `fromDomain()` en DTOs de request.

## 7. Sesión y preferencias

`SessionPreferences` guarda en DataStore:

- `auth_token`
- `merchant_id`
- `username`
- `is_verified`
- `company_name`
- `application_id`
- `ruc`

El `AuthInterceptor` lee el token de forma síncrona con `runBlocking` y lo agrega al header `Authorization: Bearer <token>`.

Si agregas un nuevo campo de sesión, actualiza:

- `SessionPreferences.kt`
- `AppModule.kt` (si es necesario)
- `AppNavigation.kt` (si afecta el destino inicial)

## 8. Cómo agregar una nueva feature

1. Crear carpeta `features/<feature>/` con las subcarpetas `data`, `domain`, `presentation`, `di`.
2. Definir modelos de dominio en `domain/model/`.
3. Definir la interfaz del repositorio en `domain/repository/`.
4. Crear API y DTOs en `data/remote/`.
5. Implementar el repositorio en `data/repository/`.
6. Crear use case(s) en `domain/usecase/`.
7. Crear `UiState`, `ViewModel` y `Screen` en `presentation/`.
8. Crear `Destination` y `NavGraph` en `presentation/navigation/` si el feature tiene múltiples pantallas.
9. Crear módulo Hilt en `di/`.
10. Registrar el módulo Hilt en `SmartCartApplication` si no se detecta automáticamente.
11. Agregar el punto de entrada en `AppNavigation.kt` si es una pantalla top-level.

## 9. Build y ejecución

- Requisito: Android SDK, JDK 11+ y `JAVA_HOME` configurado.
- Compilar debug:
  ```bash
  ./gradlew :app:compileDebugKotlin
  ```
- Instalar en dispositivo:
  ```bash
  ./gradlew :app:installDebug
  ```
- Limpiar:
  ```bash
  ./gradlew clean
  ```

## 10. API backend

La URL base está en `BuildConfig.API_BASE_URL`:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"https://smartcart-api-production.up.railway.app/api/v1/\"")
```

Endpoints conocidos:

- Auth: `POST auth/sign-in`, `POST auth/sign-up`
- Verification: `POST verification/verify`
- Store: `POST store-management/stores`, `GET store-management/stores/{storeId}`

Nota: Algunos endpoints devuelven respuestas no estandarizadas. Por ejemplo, `POST store-management/stores` devuelve solo el `storeId` como número, no el objeto completo. El repositorio debe manejar esto extrayendo el ID y haciendo un GET posterior si es necesario.

## 11. Notas importantes para agentes

- No modifiques la lógica de negocio existente en tests; enfócate en arreglar errores causados por cambios de interfaz.
- Mantén los cambios mínimos y coherentes con el estilo del proyecto.
- Siempre que agregues un campo nuevo a un modelo de dominio, evalúa si afecta a los DTOs, ViewModels y UI.
- Para eventos de navegación tras una operación exitosa, usa `LaunchedEffect(uiState.isSuccess)` y proporciona un método en el ViewModel para consumir/resetear el flag (`onStoreCreated()`, etc.).
- No ejecutes `git commit`, `git push` ni otras mutaciones de git a menos que el usuario lo solicite explícitamente.
- Si instalas herramientas o dependencias nuevas, hazlo dentro del entorno del proyecto (no en el sistema operativo anfitrión) y pide confirmación cuando sea necesario.

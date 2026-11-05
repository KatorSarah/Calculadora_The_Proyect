# Calculadora iOS en Android

Este proyecto es una aplicación Android nativa desarrollada con Kotlin y Jetpack Compose.

## Objetivo

Implementar una calculadora inspirada en la interfaz de iOS, siguiendo la arquitectura MVVM y manteniendo la lógica matemática separada de la vista.

## Arquitectura

- Model: contiene los cálculos, validaciones y estructuras de datos.
- ViewModel: gestiona el estado observable y las acciones del usuario.
- View: presenta la interfaz construida con Composables.

## Requisitos

- Android Studio
- JDK 17
- Android SDK 34

## Ejecución

1. Abre el proyecto en Android Studio.
2. Sincroniza Gradle.
3. Ejecuta la app en un emulador o dispositivo.

## Funcionalidades

- Operaciones básicas
- Decimal
- Cambio de signo
- Porcentaje
- Historial
- Navegación entre pantallas

## Estructura principal

- app/src/main/java/com/example/calculadora/MainActivity.kt
- app/src/main/java/com/example/calculadora/model/CalculatorEngine.kt
- app/src/main/java/com/example/calculadora/viewmodel/CalculatorViewModel.kt
- app/src/main/java/com/example/calculadora/ui/screens/CalculatorScreen.kt
- app/src/main/java/com/example/calculadora/ui/screens/HistoryScreen.kt
- app/src/main/res/values/strings.xml
- app/src/main/res/values/colors.xml

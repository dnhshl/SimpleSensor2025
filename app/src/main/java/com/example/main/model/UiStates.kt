package com.example.main.model

import com.patrykandpatrick.vico.core.cartesian.CartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import kotlinx.serialization.Serializable


// Datenklassen für den UI-Zustand
// ----------------------------------------------------------------


// Persistenter UI-Zustand
@Serializable
data class PersistantUiState(
    val dummy: String = "dummy"
)

// Nicht persistenter UI-Zustand
data class UiState(
    val fitnessData: FitnessData? = null,
    val modelProducer: CartesianChartModelProducer = CartesianChartModelProducer(),
    val chart: CartesianChart? = null
)




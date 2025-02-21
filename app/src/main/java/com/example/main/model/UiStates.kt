package com.example.main.model

import android.hardware.Sensor
import android.location.Location
import androidx.compose.ui.graphics.Color
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
)




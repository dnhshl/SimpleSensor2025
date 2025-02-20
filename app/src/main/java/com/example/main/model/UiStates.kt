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
    val name: String = ""
)

// Nicht persistenter UI-Zustand
data class UiState(
    val sensors: List<Sensor> = emptyList(),
    val gyro: List<Float> = emptyList(),
    val acceleration: List<Float> = emptyList(),
    val backgroundColor: Color = Color.White,
    val location: Location? = null
)




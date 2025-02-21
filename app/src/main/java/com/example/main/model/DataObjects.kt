package com.example.main.model

import kotlinx.serialization.Serializable

@Serializable
data class FitnessData(
    val fitness: Double = 0.0,
    val puls: Int = 0,
    val isotimestamp: String = ""
)
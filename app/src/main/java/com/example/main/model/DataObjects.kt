package com.example.main.model

import kotlinx.serialization.Serializable
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

const val MAXPULS = 200
const val MINPULS = 40
const val MAX_FITNESS_DATA = 20



@Serializable
data class FitnessData(
    val fitness: Double = 0.0,
    val puls: Int = 0,
    val isotimestamp: String = "2025-01-01T00:00:00.000000+00:00",
) {
    val formattedTimestamp: String
        get() {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss 'Uhr'")
            val zonedDateTime = ZonedDateTime.parse(isotimestamp)
                .withZoneSameInstant(ZoneId.of("Europe/Berlin"))
            return formatter.format(zonedDateTime)
        }

    val axisLabel: String
        get() {
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val zonedDateTime = ZonedDateTime.parse(isotimestamp)
                .withZoneSameInstant(ZoneId.of("Europe/Berlin"))
            return formatter.format(zonedDateTime)
        }
}
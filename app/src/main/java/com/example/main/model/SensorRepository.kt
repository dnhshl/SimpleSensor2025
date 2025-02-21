package com.example.main.model

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SensorRepository(private val context: Context) {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun getAllSensors(): List<Sensor> {
        return sensorManager.getSensorList(Sensor.TYPE_ALL)
    }

    fun getSensorUpdates(sensorType: Int, delay: Int = SensorManager.SENSOR_DELAY_NORMAL): Flow<SensorEvent> =
        callbackFlow {
            val sensor = sensorManager.getDefaultSensor(sensorType)
            if (sensor == null) {
                close(Exception("Sensor of type $sensorType not available"))
                return@callbackFlow
            }
            val sensorListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let {
                        trySend(it).isSuccess
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                    // No-op
                }
            }

            sensorManager.registerListener(sensorListener, sensor, delay)
                .also { success ->
                    if (!success) close(Exception("Failed to register sensor listener"))
                }

            awaitClose {
                sensorManager.unregisterListener(sensorListener)
            }
        }
}

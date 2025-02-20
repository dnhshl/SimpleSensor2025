package com.example.main.model

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {

    val snackbarHostState = SnackbarHostState()
    private val Context.dataStore by preferencesDataStore(name = "ui_state")
    private val dataStore = application.dataStore
    private val datastoreManager = DatastoreManager(dataStore)


    // Zugriff auf Sensoren via SensorManager
    private val sensorManager =
        application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)


    // Zugriff auf Gyroskop Sensor
    private val gyroSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val gyroSensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                _state.value = _state.value.copy(gyro = it.values.toList())
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }
    }

    // Zugriff auf Beschleunigungssensor
    private val accelerometerSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val accelerometerSensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                _state.value = _state.value.copy(acceleration = it.values.toList())
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    // Zugriff auf Location
    private val locationRepository = LocationRepository(application)


    // Persistenter State

    private val _pState = MutableStateFlow(PersistantUiState())
    val pState: StateFlow<PersistantUiState> get() = _pState

    // non persistenter State

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> get() = _state


    init {

        // Sensoren im State speichern
        _state.value = _state.value.copy(sensors = sensors)


        // Hier können "Beobachter" auf Zustandsänderungen initialisiert werden
        // z.B. um den UI-Zustand zu speichern oder um auf Änderungen zu reagieren

        // Gyroskop Sensor überwachen
        gyroSensor?.also {
            sensorManager.registerListener(
                gyroSensorListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        // Beschleunigungssensor überwachen
        accelerometerSensor?.also {
            sensorManager.registerListener(
                accelerometerSensorListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        // Location überwachen
        viewModelScope.launch {
            locationRepository.getLocationUpdates().collectLatest { location ->
                _state.value = _state.value.copy(location = location)
            }
        }


        // Lade den persistenten UI-Zustand
        viewModelScope.launch {
            datastoreManager.getPersistantState().collectLatest { persistedState ->
                _pState.value = persistedState
            }
            Log.i(">>>>>", "loading Preferences: ${_pState.value}")
        }


        // Überwache den persistant state und speichere ihn bei Änderungen
        viewModelScope.launch {
            _pState.collectLatest {
                val pState = _pState.value
                datastoreManager.savePersitantState(pState)
            }
        }

        // Überwache den state und triggere Aktionen bei bestimmeten Zuständen
        // Hier als Beispiel: Ändere die Hintergrundfarbe bei bestimmten Gyro-Werten

        viewModelScope.launch {
            _state.collectLatest {
                val gyroY = _state.value.gyro.getOrElse(1) { 0f }
                if (gyroY > 0.5) _state.value = _state.value.copy(backgroundColor = Color.Red)
                if (gyroY < -0.5) _state.value = _state.value.copy(backgroundColor = Color.Green)
            }
        }

    }


    // Actions
    // ------------------------------------------------------------------------------


    // Setze den Namen im persistanten State

    fun onNameChange(name: String) {
        _pState.value = _pState.value.copy(name = name)
    }


    // Ab hier Helper Funktionen
    // ------------------------------------------------------------------------------


    // Snackbar
    // ------------------------------------------------------------------------------

    fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        Log.i(">>>>>", "showSnackbar: $message")
        viewModelScope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = duration
            )
        }
    }


    // Zugriff auf String Ressourcen
    // ------------------------------------------------------------------------------
    private fun getStringRessource(resId: Int): String {
        return getApplication<Application>().getString(resId)
    }

    // Cleanup bei ViewModel-Ende
    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(gyroSensorListener)
        sensorManager.unregisterListener(accelerometerSensorListener)
    }
}

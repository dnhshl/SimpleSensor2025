package com.example.main.model

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {

    val snackbarHostState = SnackbarHostState()
    private val Context.dataStore by preferencesDataStore(name = "ui_state")
    private val dataStore = application.dataStore
    private val datastoreManager = DatastoreManager(dataStore)


    // Zugriff auf Sensoren
    val sensorRepository = SensorRepository(getApplication())


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
        val sensorList = sensorRepository.getAllSensors()
        _state.value = _state.value.copy(sensors = sensorList)


        // Hier können "Beobachter" auf Zustandsänderungen initialisiert werden
        // z.B. um den UI-Zustand zu speichern oder um auf Änderungen zu reagieren

        // Überwache Gyro Sensor
        viewModelScope.launch {
            sensorRepository.getSensorUpdates(Sensor.TYPE_GYROSCOPE).collect { event ->
                _state.value = _state.value.copy(gyro = event.values.toList())
            }
        }

        // Überwache Accelerometer Sensor
        viewModelScope.launch {
            sensorRepository.getSensorUpdates(Sensor.TYPE_ACCELEROMETER).collect { event ->
                _state.value = _state.value.copy(acceleration = event.values.toList())
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

    private var locationJob: Job? = null

    fun startLocationUpdates() {
        locationJob = viewModelScope.launch {
            locationRepository.getLocationUpdates().collectLatest { location ->
                _state.value = _state.value.copy(location = location)
            }
        }
    }

    fun stopLocationUpdates() {
        locationJob?.cancel()
        locationJob = null
    }

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

}

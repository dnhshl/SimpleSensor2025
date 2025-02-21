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
import com.example.main.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json.Default.decodeFromString
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class MainViewModel(application: Application) : AndroidViewModel(application) {

    val snackbarHostState = SnackbarHostState()
    private val Context.dataStore by preferencesDataStore(name = "ui_state")
    private val dataStore = application.dataStore
    private val datastoreManager = DatastoreManager(dataStore)



    // Network Repository einbinden
    private val URL = "https://fitnessdata-436188705757.us-central1.run.app/"
    private val networkRepository = NetworkRepository()


    // Persistenter State

    private val _pState = MutableStateFlow(PersistantUiState())
    val pState: StateFlow<PersistantUiState> get() = _pState

    // non persistenter State

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> get() = _state


    init {


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


    }


    // Actions
    // ------------------------------------------------------------------------------


    fun fetchJsonData() {
        viewModelScope.launch {
            try {
                val jsonData = networkRepository.getJsonData(URL)
                val fitnessData = parseFitnessData(jsonData)
                _state.value = _state.value.copy(fitnessData = fitnessData)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to fetch JSON data", e)
                showSnackbar(getStringRessource(R.string.error_fetching_data))
            }
        }
    }

    private var fetchJob: Job? = null

    fun startFetchingData(intervalMillis: Long) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            while (isActive) {
                fetchJsonData()
                delay(intervalMillis)
            }
        }
    }

    fun stopFetchingData() {
        fetchJob?.cancel()
        fetchJob = null
    }



    // Ab hier Helper Funktionen
    // ------------------------------------------------------------------------------

    private fun parseFitnessData(jsonString: String): FitnessData {
        val fitnessData = decodeFromString<FitnessData>(jsonString)
        val parsedTimestamp = parseIsoTimestamp(fitnessData.isotimestamp)
        return fitnessData.copy(isotimestamp = parsedTimestamp)
    }

    private fun parseIsoTimestamp(isoTimestamp: String): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss 'Uhr'")
        val zonedDateTime = ZonedDateTime.parse(isoTimestamp)
            .withZoneSameInstant(ZoneId.of("Europe/Berlin"))
        return formatter.format(zonedDateTime)
    }

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

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
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
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
import kotlin.random.Random


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

        viewModelScope.launch {
            _state.value.modelProducer.runTransaction {
                lineSeries {
                    series(13, 8, 7, 12, 0, 1, 15, 14, 0, 11, 6, 12, 0, 11, 12, 11)
                }
            }
        }


    }


    // Actions
    // ------------------------------------------------------------------------------



    fun fetchJsonData() {
        viewModelScope.launch {
            try {
                val jsonData = networkRepository.getJsonData(URL)
                Log.i(">>>>>", "fetchJsonData: $jsonData")
                val fitnessData = parseFitnessData(jsonData)
                Log.i(">>>>>", "fetchJsonData: $fitnessData")
                val currentList = _state.value.fitnessDataList
                val newList = (currentList + fitnessData).takeLast(MAX_FITNESS_DATA)
                val labelList = newList.map { it.axisLabel }
                _state.value.modelProducer.runTransaction {
                    lineSeries {
                        series( newList.map { it.puls })
                        extras { it[BottomAxisLabelKey] = labelList }
                    }
                }

                _state.value = _state.value.copy(
                    fitnessData = fitnessData,
                    fitnessDataList = newList
                )
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
            Log.i(">>>>>", "startFetchingData")
            while (isActive) {
                fetchJsonData()
                delay(intervalMillis)
            }
        }
    }

    fun stopFetchingData() {
        Log.i(">>>>>", "stopFetchingData")
        fetchJob?.cancel()
        fetchJob = null
    }

    private var fetchChartDataJob: Job? = null

    fun startFetchingChartData(intervalMillis: Long) {
        fetchChartDataJob?.cancel()
        _state.value = _state.value.copy(fitnessDataList = emptyList())
        fetchChartDataJob = viewModelScope.launch {
            while (isActive) {
                fetchJsonData()
                delay(intervalMillis)
            }
        }
    }

    fun stopFetchingChartData() {
        fetchChartDataJob?.cancel()
        fetchChartDataJob = null
    }



    // Ab hier Helper Funktionen
    // ------------------------------------------------------------------------------

    private fun parseFitnessData(jsonString: String): FitnessData {
        val fitnessData = decodeFromString<FitnessData>(jsonString)
        return fitnessData
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

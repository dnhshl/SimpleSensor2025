package com.example.main.model

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.main.R
import com.example.main.ui.screens.BottomAxisLabelKey
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json.Default.decodeFromString


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



    }


    // Actions
    // ------------------------------------------------------------------------------

    fun getFitnessData() {
        fetchFitnessData()
        if (fitnessDataBuffer.isNotEmpty()) {
            val fitnessData = fitnessDataBuffer.last()
            _state.value = _state.value.copy(fitnessData = fitnessData)
        }
    }


    private var fetchJob: Job? = null

    fun startFetchingData(intervalMillis: Long) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            Log.i(">>>>>", "startFetchingData")
            while (isActive) {
                getFitnessData()
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
        fetchChartDataJob = viewModelScope.launch {
            while (isActive) {
                fetchFitnessData()
                updateChartData()
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

    fun fetchFitnessData() {
        viewModelScope.launch {
            try {
                val jsonData = networkRepository.getJsonData(URL)
                Log.i(">>>>>", "jsonData: $jsonData")
                val fitnessData = parseFitnessData(jsonData)
                fitnessData?.let { addFitnessData(it) }
                Log.i(">>>>>", "fitnessData: $fitnessData")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to fetch JSON data", e)
                showSnackbar(getStringRessource(R.string.error_fetching_data))
            }
        }
    }

    private fun parseFitnessData(jsonString: String): FitnessData? {
        return try {
            decodeFromString<FitnessData>(jsonString)
        } catch (e: Exception) {
            null
        }
    }

    private val fitnessDataBuffer = ArrayDeque<FitnessData>()

    private fun addFitnessData(data: FitnessData) {
        if (fitnessDataBuffer.size == MAX_FITNESS_DATA) {
            fitnessDataBuffer.removeFirst()
        }
        fitnessDataBuffer.addLast(data)
    }

    private suspend fun updateChartData() {
        if (fitnessDataBuffer.isEmpty()) return
        val labelList = fitnessDataBuffer.map { it.axisLabel }
        _state.value.modelProducer.runTransaction {
            lineSeries {
                series(fitnessDataBuffer.map { it.puls })
                extras { it[BottomAxisLabelKey] = labelList }
            }
        }
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

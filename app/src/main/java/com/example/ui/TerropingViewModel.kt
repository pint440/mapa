package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.ReporteEntity
import com.example.data.database.SismoEntity
import com.example.data.database.TerropingDatabase
import com.example.data.network.GeminiClient
import com.example.data.network.UsgsApiService
import com.example.data.repository.TerropingRepository
import com.example.data.sensor.SeismographSensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TerropingViewModel(application: Application) : AndroidViewModel(application) {
    private val database = TerropingDatabase.getDatabase(application)
    private val apiService = UsgsApiService.create()
    private val repository = TerropingRepository(
        sismoDao = database.sismoDao(),
        reporteDao = database.reporteDao(),
        apiService = apiService
    )
    
    val sensorManager = SeismographSensorManager(application)

    val sismos: StateFlow<List<SismoEntity>> = repository.allSismos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteSismos: StateFlow<List<SismoEntity>> = repository.favoriteSismos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reportes: StateFlow<List<ReporteEntity>> = repository.allReportes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vibrationData: StateFlow<Float> = sensorManager.vibrationData
    val isSensorAvailable: StateFlow<Boolean> = sensorManager.isSensorAvailable

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedSismo = MutableStateFlow<SismoEntity?>(null)
    val selectedSismo: StateFlow<SismoEntity?> = _selectedSismo.asStateFlow()

    private val _aiExplanation = MutableStateFlow<String?>(null)
    val aiExplanation: StateFlow<String?> = _aiExplanation.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    init {
        refreshSismos()
        sensorManager.startListening()
    }

    fun refreshSismos() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _errorMessage.value = null
            val result = repository.refreshSismos()
            if (result.isFailure) {
                _errorMessage.value = "Error de conexión al obtener sismos recientes de la USGS. Mostrando datos locales offline."
            }
            _isRefreshing.value = false
        }
    }

    fun selectSismo(sismo: SismoEntity?) {
        _selectedSismo.value = sismo
        _aiExplanation.value = null
        if (sismo != null) {
            getAiExplanation(sismo)
        }
    }

    private fun getAiExplanation(sismo: SismoEntity) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val prompt = """
                Eres el Asistente Sismológico de Terroping para Venezuela.
                Analiza el siguiente evento sísmico en tiempo real:
                - ID: ${sismo.id}
                - Magnitud: ${sismo.mag}
                - Ubicación: ${sismo.place}
                - Latitud: ${sismo.latitude}, Longitud: ${sismo.longitude}
                - Profundidad: ${sismo.depth} km
                
                Explica brevemente en español y con calma:
                1. ¿Qué tan peligroso es y su impacto potencial en Venezuela?
                2. ¿Con qué falla geológica principal de Venezuela se asocia (Boconó, San Sebastián, El Pilar, etc.)?
                3. Recomendación de seguridad clave en la zona.
                Sé breve (menos de 150 palabras), directo, tranquilizador y educativo.
            """.trimIndent()
            
            _aiExplanation.value = GeminiClient.askGemini(prompt)
            _isAiLoading.value = false
        }
    }

    fun toggleFavorite(sismo: SismoEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(sismo.id)
            if (_selectedSismo.value?.id == sismo.id) {
                _selectedSismo.value = _selectedSismo.value?.copy(isFavorite = !sismo.isFavorite)
            }
        }
    }

    fun reportFelt(sismo: SismoEntity) {
        viewModelScope.launch {
            repository.reportFeltByUser(sismo.id)
            if (_selectedSismo.value?.id == sismo.id) {
                _selectedSismo.value = _selectedSismo.value?.copy(feltByUser = true)
            }
        }
    }

    fun submitReporte(intensity: String, city: String, comments: String) {
        viewModelScope.launch {
            repository.createReporte(intensity, city, comments)
        }
    }

    fun deleteReporte(id: Int) {
        viewModelScope.launch {
            repository.deleteReporte(id)
        }
    }

    fun triggerManualPulse(): Float {
        // Simulates a tiny shock pulse when tapping the seismograph screen
        return kotlin.random.Random.nextFloat() * (4.0f - 1.5f) + 1.5f
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.stopListening()
    }
}

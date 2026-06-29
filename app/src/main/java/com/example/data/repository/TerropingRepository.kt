package com.example.data.repository

import android.util.Log
import com.example.data.database.ReporteDao
import com.example.data.database.ReporteEntity
import com.example.data.database.SismoDao
import com.example.data.database.SismoEntity
import com.example.data.network.UsgsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TerropingRepository(
    private val sismoDao: SismoDao,
    private val reporteDao: ReporteDao,
    private val apiService: UsgsApiService
) {
    val allSismos: Flow<List<SismoEntity>> = sismoDao.getAllSismos()
    val favoriteSismos: Flow<List<SismoEntity>> = sismoDao.getFavoriteSismos()
    val allReportes: Flow<List<ReporteEntity>> = reporteDao.getAllReportes()

    suspend fun refreshSismos(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getVenezuelaEarthquakes()
            val mappedEntities = response.features.map { feature ->
                val coords = feature.geometry.coordinates
                val lon = coords.getOrNull(0) ?: 0.0
                val lat = coords.getOrNull(1) ?: 0.0
                val depth = coords.getOrNull(2) ?: 0.0
                
                SismoEntity(
                    id = feature.id,
                    mag = feature.properties.mag ?: 0.0,
                    place = feature.properties.place ?: "Ubicación desconocida",
                    time = feature.properties.time ?: System.currentTimeMillis(),
                    latitude = lat,
                    longitude = lon,
                    depth = depth,
                    isFavorite = false,
                    feltByUser = false
                )
            }
            if (mappedEntities.isNotEmpty()) {
                // Clear old items that aren't favorites to save database space, if any
                sismoDao.clearNonFavorites()
                // Insert new ones
                sismoDao.insertSismos(mappedEntities)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TerropingRepository", "Error refreshing earthquakes", e)
            Result.failure(e)
        }
    }

    suspend fun toggleFavorite(id: String) = withContext(Dispatchers.IO) {
        val sismo = sismoDao.getSismoById(id)
        if (sismo != null) {
            sismoDao.updateSismo(sismo.copy(isFavorite = !sismo.isFavorite))
        }
    }

    suspend fun reportFeltByUser(id: String) = withContext(Dispatchers.IO) {
        val sismo = sismoDao.getSismoById(id)
        if (sismo != null) {
            sismoDao.updateSismo(sismo.copy(feltByUser = true))
        }
    }

    suspend fun createReporte(intensity: String, city: String, comments: String) = withContext(Dispatchers.IO) {
        val newReporte = ReporteEntity(
            intensity = intensity,
            city = city,
            comments = comments
        )
        reporteDao.insertReporte(newReporte)
    }

    suspend fun deleteReporte(id: Int) = withContext(Dispatchers.IO) {
        reporteDao.deleteReporte(id)
    }
}

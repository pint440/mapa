package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sismos")
data class SismoEntity(
    @PrimaryKey val id: String,
    val mag: Double,
    val place: String,
    val time: Long,
    val latitude: Double,
    val longitude: Double,
    val depth: Double,
    val isFavorite: Boolean = false,
    val feltByUser: Boolean = false
)

@Entity(tableName = "reportes")
data class ReporteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val intensity: String, // Leve, Moderado, Fuerte
    val city: String,
    val comments: String,
    val timestamp: Long = System.currentTimeMillis()
)

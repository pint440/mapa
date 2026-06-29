package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SismoDao {
    @Query("SELECT * FROM sismos ORDER BY time DESC")
    fun getAllSismos(): Flow<List<SismoEntity>>

    @Query("SELECT * FROM sismos WHERE isFavorite = 1 ORDER BY time DESC")
    fun getFavoriteSismos(): Flow<List<SismoEntity>>

    @Query("SELECT * FROM sismos WHERE id = :id LIMIT 1")
    suspend fun getSismoById(id: String): SismoEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSismos(sismos: List<SismoEntity>)

    @Update
    suspend fun updateSismo(sismo: SismoEntity)

    @Query("DELETE FROM sismos WHERE isFavorite = 0")
    suspend fun clearNonFavorites()
}

@Dao
interface ReporteDao {
    @Query("SELECT * FROM reportes ORDER BY timestamp DESC")
    fun getAllReportes(): Flow<List<ReporteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReporte(reporte: ReporteEntity)

    @Query("DELETE FROM reportes WHERE id = :id")
    suspend fun deleteReporte(id: Int)
}

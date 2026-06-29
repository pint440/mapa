package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SismoEntity::class, ReporteEntity::class], version = 1, exportSchema = false)
abstract class TerropingDatabase : RoomDatabase() {
    abstract fun sismoDao(): SismoDao
    abstract fun reporteDao(): ReporteDao

    companion object {
        @Volatile
        private var INSTANCE: TerropingDatabase? = null

        fun getDatabase(context: Context): TerropingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TerropingDatabase::class.java,
                    "terroping_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

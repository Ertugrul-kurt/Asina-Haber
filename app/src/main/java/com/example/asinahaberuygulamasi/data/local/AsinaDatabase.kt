package com.example.asinahaberuygulamasi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SavedNews::class], version = 1, exportSchema = false)
abstract class AsinaDatabase : RoomDatabase() {

    abstract fun savedNewsDao(): SavedNewsDao

    companion object {
        @Volatile
        private var INSTANCE: AsinaDatabase? = null

        fun getDatabase(context: Context): AsinaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AsinaDatabase::class.java,
                    "asina_haber_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}


package com.example.mainaplicationpsm.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Borrador::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun borradorDao(): BorradorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mi_base_local_db" // Nombre del archivo .db en el celular
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
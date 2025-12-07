package com.example.mainaplicationpsm.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BorradorDao {
    // Guardar un nuevo borrador
    @Insert
    suspend fun insertar(borrador: Borrador)

    // Obtener todos los borradores (del más nuevo al más viejo)
    @Query("SELECT * FROM borradores ORDER BY fecha DESC")
    suspend fun obtenerTodos(): List<Borrador>

    // Borrar un borrador (ej. cuando ya se subió a internet)s
    @Delete
    suspend fun eliminar(borrador: Borrador)
}
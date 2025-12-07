package com.example.mainaplicationpsm.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "borradores")
data class Borrador(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String, // Aunque lo tenemos fijo en el código, lo guardamos
    val descripcion: String,
    val uriFoto: String?, // Guardamos la URI de la foto como String
    val fecha: Long = System.currentTimeMillis() // Para ordenar por el más reciente
)
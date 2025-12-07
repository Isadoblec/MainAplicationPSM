package com.example.mainaplicationpsm.api

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("status") val status: String,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("id") val id: Int? = null
)
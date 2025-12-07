package com.example.mainaplicationpsm.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("subir_publicacion.php")
    suspend fun subirPublicacion(
        @Part("id_usuario") idUsuario: RequestBody,
        @Part("id_foro") idForo: RequestBody,
        @Part("titulo") titulo: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part("es_borrador") esBorrador: RequestBody,
        @Part foto: MultipartBody.Part?
    ): Response<ApiResponse>
}
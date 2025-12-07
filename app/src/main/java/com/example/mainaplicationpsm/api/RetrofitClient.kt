package com.example.mainaplicationpsm.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ⚠️ OJO: CAMBIA ESTO POR TU URL DE NGROK ACTUAL
    // Debe terminar con "/" y tener el nombre de la carpeta de htdocs
    // Ejemplo: "https://a1b2-c3d4.ngrok-free.app/SistemasMoviles/"
    private const val BASE_URL = " https://fidel-crackliest-larhonda.ngrok-free.dev/SistemasMoviles/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}
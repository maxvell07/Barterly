package com.example.barterly.service

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitClient {


    // Создание логирующего interceptor с уровнем BODY
    val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("OkHttp", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private const val BASE_URL = " https://0247-94-142-136-113.ngrok-free.app/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val fileService: FileService by lazy {
        retrofit.create(FileService::class.java)
    }
}
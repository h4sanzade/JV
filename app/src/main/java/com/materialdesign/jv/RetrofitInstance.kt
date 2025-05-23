package com.materialdesign.jv

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue

object RetrofitInstance {
    private const val BASE_URL = "https://be2e-212-47-129-161.ngrok-free.app/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}
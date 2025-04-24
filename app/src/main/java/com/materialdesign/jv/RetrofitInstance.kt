package com.materialdesign.jv

import kotlin.getValue

object RetrofitInstance {
    private const val BASE_URL = "https://1cc3-212-47-137-95.ngrok-free.app/"

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

package com.materialdesign.jv

interface AuthApiService {
    @POST("API/V1/LOGIN")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}

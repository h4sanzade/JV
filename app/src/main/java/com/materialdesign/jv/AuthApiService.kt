package com.materialdesign.jv

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/v1/auth/sign-up")
    suspend fun signUp(@Body request: SignUpRequest): Response<LoginResponse>
}
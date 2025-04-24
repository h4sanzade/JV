package com.materialdesign.jv

data class LoginResponse(
    val data: TokenData
)

data class TokenData(
    val token: String
)

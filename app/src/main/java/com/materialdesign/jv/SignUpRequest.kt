package com.materialdesign.jv

data class SignUpRequest(
    val phoneNumber: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)
package com.example.moveon.data.remote.dto

data class LoginRequest(
    val loginType: String,
    val email: String? = null,
    val phone: String? = null,
    val password: String
)

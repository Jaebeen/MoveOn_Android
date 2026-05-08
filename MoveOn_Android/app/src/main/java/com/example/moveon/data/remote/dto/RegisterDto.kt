package com.example.moveon.data.remote.dto

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String
)

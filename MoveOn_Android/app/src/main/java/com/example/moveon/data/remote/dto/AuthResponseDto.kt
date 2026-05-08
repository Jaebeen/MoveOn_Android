package com.example.moveon.data.remote.dto

data class AuthResponse(
    val user: AuthUserDto,
    val token: String
)

data class AuthUserDto(
    val _id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val createdAt: String,
    val __v: Int? = null
)

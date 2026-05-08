package com.example.moveon.data.remote.api

import com.example.moveon.data.remote.dto.AuthResponse
import com.example.moveon.data.remote.dto.LoginRequest
import com.example.moveon.data.remote.dto.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MoveOnApiService {
    @POST("auth/register")
    fun register(
        @Body request: RegisterRequest
    ): Call<AuthResponse>

    @POST("auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<AuthResponse>
}

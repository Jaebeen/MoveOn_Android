package com.example.moveon.data.repository

import com.example.moveon.data.remote.dto.AuthResponse
import com.example.moveon.data.remote.dto.ErrorResponse
import com.example.moveon.data.remote.dto.RegisterRequest
import com.example.moveon.data.remote.network.RetrofitClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpRepository {
    fun register(
        request: RegisterRequest,
        callback: (Result<AuthResponse>) -> Unit
    ) {
        RetrofitClient.apiService.register(request)
            .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    val body = response.body()

                    if (response.isSuccessful && body != null) {
                        callback(Result.success(body))
                    } else {
                        callback(Result.failure(Throwable(parseErrorMessage(response))))
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, throwable: Throwable) {
                    callback(Result.failure(throwable))
                }
            })
    }

    private fun parseErrorMessage(response: Response<AuthResponse>): String? {
        val errorBody = response.errorBody()?.string().orEmpty()
        if (errorBody.isBlank()) {
            return null
        }

        return runCatching {
            Gson().fromJson(errorBody, ErrorResponse::class.java).error
        }.getOrNull().takeUnless { it.isNullOrBlank() }
    }
}

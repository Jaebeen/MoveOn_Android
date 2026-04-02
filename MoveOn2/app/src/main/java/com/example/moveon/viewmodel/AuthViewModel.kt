package com.example.moveon.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moveon.repository.AuthRepository

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    val loginResult = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun login(email: String, password: String) {
        repository.login(email, password) { success, error ->
            loginResult.postValue(success)
            error?.let { errorMessage.postValue(it) }
        }
    }
}
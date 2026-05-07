package com.example.moveon.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moveon.data.repository.AuthRepository

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()
    val loginEnabled = MutableLiveData<Boolean>()
    val loginResult = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun login(email: String, password: String) {
        repository.login(email, password) { success, error ->
            loginResult.postValue(success)
            error?.let { errorMessage.postValue(it) }
        }
    }

    private fun checkValidation(email: String, password: String) {
        var isValid = true

        if (email.isEmpty()) {
            emailError.value = ""
        }
    }

}

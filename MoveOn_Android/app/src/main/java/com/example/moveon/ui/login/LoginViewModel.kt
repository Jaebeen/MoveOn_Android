package com.example.moveon.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moveon.R
import com.example.moveon.data.remote.dto.LoginRequest
import com.example.moveon.data.repository.AuthRepository

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()

    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()
    val loginEnabled = MutableLiveData<Boolean>()
    val loginResult = MutableLiveData<Boolean>()
    val toastMessage = MutableLiveData<LoginToastMessage>()

    fun loginWithEmail(email: String, password: String) {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isEmpty()) {
            toastMessage.value = LoginToastMessage(resId = R.string.login_email_valid_error_message)
            return
        }

        login(LoginRequest(loginType = LOGIN_TYPE_EMAIL, email = trimmedEmail, password = password))
    }

    fun loginWithPhone(phone: String, password: String) {
        val trimmedPhone = phone.trim()

        if (trimmedPhone.isEmpty()) {
            toastMessage.value = LoginToastMessage(resId = R.string.login_phone_valid_error_message)
            return
        }

        login(LoginRequest(loginType = LOGIN_TYPE_PHONE, phone = trimmedPhone, password = password))
    }

    private fun login(request: LoginRequest) {
        if (request.password.isEmpty()) {
            toastMessage.value = LoginToastMessage(resId = R.string.login_password_valid_error_message)
            return
        }

        repository.login(request) { result ->
            result.fold(
                onSuccess = {
                    loginResult.postValue(true)
                },
                onFailure = { throwable ->
                    loginResult.postValue(false)
                    val serverMessage = throwable.message
                    if (serverMessage.isNullOrBlank()) {
                        toastMessage.postValue(LoginToastMessage(resId = R.string.toast_login_failure_message))
                    } else {
                        toastMessage.postValue(LoginToastMessage(message = serverMessage))
                    }
                }
            )
        }
    }

    private fun checkValidation(email: String, password: String) {
        var isValid = true

        if (email.isEmpty()) {
            emailError.value = ""
        }
    }

    companion object {
        private const val LOGIN_TYPE_EMAIL = "email"
        private const val LOGIN_TYPE_PHONE = "phone"
    }
}

data class LoginToastMessage(
    val message: String? = null,
    val resId: Int? = null
)

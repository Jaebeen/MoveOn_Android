package com.example.moveon.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moveon.R
import com.example.moveon.data.remote.dto.RegisterRequest
import com.example.moveon.data.repository.SignUpRepository

class SignUpViewModel : ViewModel() {
    private val repository = SignUpRepository()

    private val _uiState = MutableLiveData(SignUpUiState())
    val uiState: LiveData<SignUpUiState> = _uiState

    private var selectedRole: String = ROLE_USER

    fun selectUserRole() {
        selectedRole = ROLE_USER
        _uiState.value = _uiState.value?.copy(selectedRole = selectedRole)
    }

    fun selectAdminRole() {
        selectedRole = ROLE_FACILITY_ADMIN
        _uiState.value = _uiState.value?.copy(selectedRole = selectedRole)
    }

    fun register(name: String, email: String, phone: String, password: String, passwordConfirm: String) {

        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedPhone = phone.trim()
        val nameError = if (trimmedName.isEmpty()) R.string.signup_name_valid_error_message else null
        val emailError = if (trimmedEmail.isEmpty()) R.string.signup_email_valid_error_message else null
        val phoneError = if (trimmedPhone.isEmpty()) R.string.signup_phone_valid_error_message else null
        val passwordError = if (password.isEmpty()) R.string.signup_password_valid_error_message else null
        val passwordConfirmError = when {
            passwordConfirm.isEmpty() -> R.string.signup_password_confirm_valid_error_message
            password != passwordConfirm -> R.string.signup_password_mismatch_valid_error_message
            else -> null
        }

        if (nameError != null || emailError != null || phoneError != null || passwordError != null || passwordConfirmError != null) {

            _uiState.value = _uiState.value?.copy(
                nameError = nameError,
                emailError = emailError,
                phoneError = phoneError,
                passwordError = passwordError,
                passwordConfirmError = passwordConfirmError,
                toastMessage = null,
                toastMessageResId = null
            )
            return
        }

        _uiState.value = _uiState.value?.copy(
            isLoading = true,
            nameError = null,
            emailError = null,
            phoneError = null,
            passwordError = null,
            passwordConfirmError = null,
            toastMessage = null,
            toastMessageResId = null
        )

        repository.register(
            RegisterRequest(
                name = trimmedName,
                email = trimmedEmail,
                phone = trimmedPhone,
                password = password,
                role = selectedRole
            )
        ) { result ->
            result.fold(
                onSuccess = {
                    _uiState.postValue(
                        _uiState.value?.copy(
                            isLoading = false,
                            isSuccess = true,
                            toastMessageResId = R.string.toast_signup_success_message
                        )
                    )
                },
                onFailure = { throwable ->
                    _uiState.postValue(
                        _uiState.value?.copy(
                            isLoading = false,
                            isSuccess = false,
                            toastMessage = throwable.message.takeUnless { it.isNullOrBlank() },
                            toastMessageResId = if (throwable.message.isNullOrBlank()) {
                                R.string.toast_signup_failure_message
                            } else {
                                null
                            }
                        )
                    )
                }
            )
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value?.copy(toastMessage = null, toastMessageResId = null)
    }

    companion object {
        const val ROLE_USER = "user"
        const val ROLE_FACILITY_ADMIN = "facility_admin"
    }
}

data class SignUpUiState(
    val selectedRole: String = SignUpViewModel.ROLE_USER,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val nameError: Int? = null,
    val emailError: Int? = null,
    val phoneError: Int? = null,
    val passwordError: Int? = null,
    val passwordConfirmError: Int? = null,
    val toastMessage: String? = null,
    val toastMessageResId: Int? = null
)

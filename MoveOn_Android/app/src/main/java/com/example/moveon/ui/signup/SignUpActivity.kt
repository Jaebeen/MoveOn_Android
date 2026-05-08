package com.example.moveon.ui.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.moveon.R
import com.example.moveon.databinding.ActivitySignupBinding
import com.example.moveon.ui.login.LoginActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]

        binding.userTypeUserText.setOnClickListener {
            viewModel.selectUserRole()
        }

        binding.userTypeAdminText.setOnClickListener {
            viewModel.selectAdminRole()
        }

        binding.signupButton.setOnClickListener {
            viewModel.register(
                name = binding.nameEt.text.toString(),
                email = binding.emailEt.text.toString(),
                phone = binding.phoneEt.text.toString(),
                password = binding.passwordEt.text.toString(),
                passwordConfirm = binding.passwordConfirmEt.text.toString()
            )
        }

        viewModel.uiState.observe(this) { state ->
            updateRoleSelection(state.selectedRole)
            binding.signupButton.isEnabled = !state.isLoading
            updateErrorText(binding.nameErrorText, state.nameError)
            updateErrorText(binding.emailErrorText, state.emailError)
            updateErrorText(binding.phoneErrorText, state.phoneError)
            updateErrorText(binding.passwordErrorText, state.passwordError)
            updateErrorText(binding.passwordConfirmErrorText, state.passwordConfirmError)

            state.toastMessage?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }

            state.toastMessageResId?.let { messageResId ->
                Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }

            if (state.isSuccess) {
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(intent)
                finish()
            }
        }
    }

    private fun updateRoleSelection(role: String) {
        val isUser = role == SignUpViewModel.ROLE_USER

        binding.userTypeUserText.setBackgroundResource(
            if (isUser) R.drawable.background_login_button else R.drawable.background_login_edittext
        )
        binding.userTypeAdminText.setBackgroundResource(
            if (isUser) R.drawable.background_login_edittext else R.drawable.background_login_button
        )

        binding.userTypeUserText.setTextColor(
            ContextCompat.getColor(this, if (isUser) R.color.white else R.color.hint_color)
        )
        binding.userTypeAdminText.setTextColor(
            ContextCompat.getColor(this, if (isUser) R.color.hint_color else R.color.white)
        )
    }

    private fun updateErrorText(view: android.widget.TextView, messageResId: Int?) {
        view.text = messageResId?.let { getString(it) }.orEmpty()
        view.visibility = if (messageResId == null) View.GONE else View.VISIBLE
    }
}

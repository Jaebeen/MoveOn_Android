package com.example.moveon.ui.login

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.moveon.R
import com.example.moveon.databinding.ActivityLoginBinding
import com.example.moveon.ui.main.MainActivity
import com.example.moveon.ui.signup.SignUpActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private var loginType = LoginType.PHONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.phoneNumberLl.setOnClickListener {
            updateLoginType(LoginType.PHONE)
        }

        binding.emailIdLl.setOnClickListener {
            updateLoginType(LoginType.EMAIL)
        }

        binding.loginButton.setOnClickListener {
            when (loginType) {
                LoginType.PHONE -> {
                    viewModel.loginWithPhone(
                        binding.phoneNumberEt.text.toString(),
                        binding.passwordEt.text.toString()
                    )
                }

                LoginType.EMAIL -> {
                    viewModel.loginWithEmail(
                        binding.emailIdEt.text.toString(),
                        binding.passwordEt.text.toString()
                    )
                }
            }
        }

        binding.findPasswordText.setOnClickListener {
            val intent = Intent()
        }

        binding.signUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        viewModel.loginResult.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, R.string.toast_login_success_message, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        viewModel.toastMessage.observe(this) { toastMessage ->
            toastMessage.resId?.let { messageResId ->
                Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
                return@observe
            }

            toastMessage.message?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        updateLoginType(loginType, shouldClearInputs = false)
    }

    private fun updateLoginType(type: LoginType, shouldClearInputs: Boolean = true) {
        if (loginType == type && shouldClearInputs) {
            return
        }

        if (shouldClearInputs) {
            clearLoginInputs()
        }

        loginType = type

        val selectedColor = ContextCompat.getColor(this, R.color.login_type_selected)
        val notSelectedColor = ContextCompat.getColor(this, R.color.login_type_not_selected)
        val selectedTextColor = ContextCompat.getColor(this, R.color.white)
        val notSelectedTextColor = ContextCompat.getColor(this, R.color.hint_color)
        val isPhoneSelected = type == LoginType.PHONE

        binding.phoneNumberInputLayout.visibility = if (isPhoneSelected) View.VISIBLE else View.GONE
        binding.emailIdInputLayout.visibility = if (isPhoneSelected) View.GONE else View.VISIBLE

        binding.phoneNumberUnderline.setBackgroundColor(if (isPhoneSelected) selectedColor else notSelectedColor)
        binding.emailIdUnderline.setBackgroundColor(if (isPhoneSelected) notSelectedColor else selectedColor)

        binding.phoneNumberText.setTextColor(if (isPhoneSelected) selectedTextColor else notSelectedTextColor)
        binding.emailIdText.setTextColor(if (isPhoneSelected) notSelectedTextColor else selectedTextColor)
    }

    private enum class LoginType {
        PHONE,
        EMAIL
    }

    private fun clearLoginInputs() {
        currentFocus?.clearFocus()
        binding.phoneNumberEt.text?.clear()
        binding.emailIdEt.text?.clear()
        binding.passwordEt.text?.clear()

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}

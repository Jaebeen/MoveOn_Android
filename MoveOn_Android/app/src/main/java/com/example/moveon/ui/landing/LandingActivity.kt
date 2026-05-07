package com.example.moveon.ui.landing

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.moveon.data.local.OnboardingPreference
import com.example.moveon.databinding.ActivityLandingBinding
import com.example.moveon.ui.guide.GuideActivity
import com.example.moveon.ui.login.LoginActivity

class LandingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            switchNextActivity()
        }, LANDING_DELAY_MILLIS)
    }

    private fun switchNextActivity() {
        val nextActivity = if (OnboardingPreference.isGuideCompleted(this)) {
            LoginActivity::class.java
        } else {
            GuideActivity::class.java
        }

        startActivity(Intent(this, nextActivity))
        finish()
    }

    companion object {
        private const val LANDING_DELAY_MILLIS = 1_000L
    }
}

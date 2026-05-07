package com.example.moveon.data.local

import android.content.Context

object OnboardingPreference {
    private const val PREFERENCE_NAME = "onboarding_preference"
    private const val KEY_GUIDE_COMPLETED = "guide_completed"

    fun isGuideCompleted(context: Context): Boolean {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_GUIDE_COMPLETED, false)
    }

    fun setGuideCompleted(context: Context) {
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_GUIDE_COMPLETED, true)
            .apply()
    }
}

package com.example.moveon.repository

import com.google.firebase.auth.FirebaseAuth

class AuthRepository {
    val auth = FirebaseAuth.getInstance()

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, it.exception?.message)
                }
            }
    }
}
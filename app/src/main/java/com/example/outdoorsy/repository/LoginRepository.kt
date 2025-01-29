package com.example.outdoorsy.repository

import com.example.outdoorsy.model.dao.FirebaseModel
import com.example.outdoorsy.model.dao.UserModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepository @Inject constructor(private val firebaseModel: FirebaseModel) {

    suspend fun loginUser(email: String, password: String): UserModel? {
        return try {
            val authResult = FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .await()

            val userId = authResult.user?.uid
            if (userId != null) {
                firebaseModel.getUser(userId) // Fetch user details
            } else {
                null // Handle authentication failure
            }
        } catch (e: Exception) {
            null // Handle authentication error
        }
    }
}
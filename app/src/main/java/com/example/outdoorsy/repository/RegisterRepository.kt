package com.example.outdoorsy.repository

import com.example.outdoorsy.model.dao.FirebaseModel
import com.example.outdoorsy.model.dao.UserModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class RegisterRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseModel: FirebaseModel
) {

    suspend fun registerUser(email: String, password: String, fullname: String): Result<Unit> {
        return try {
            // Register the user using Firebase Authentication
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            // Save additional user information to Firestore
            val userId = firebaseAuth.currentUser?.uid ?: UUID.randomUUID().toString()
            val user = UserModel(
                id = userId,
                email = email,
                fullname = fullname,
                password = password // Consider hashing or encrypting the password in a production app
            )
            firebaseModel.saveUser(user)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
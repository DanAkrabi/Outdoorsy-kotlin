package com.example.outdoorsy.repository

import com.example.outdoorsy.model.FirebaseModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class RegisterRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseModel: FirebaseModel
) {

    suspend fun registerUser(email: String, password: String, fullname: String): Result<Unit> {
      return  firebaseModel.registerUser(email,password,fullname)
    }
}
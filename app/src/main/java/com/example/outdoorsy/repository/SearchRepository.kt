package com.example.outdoorsy.repository

import com.example.outdoorsy.model.dao.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SearchRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun getUsersByName(query: String): List<UserModel> {
        return try {
            firestore.collection("users")
                .orderBy("fullname")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()
                .toObjects(UserModel::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

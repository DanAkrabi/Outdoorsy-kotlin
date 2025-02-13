package com.example.outdoorsy.repository

import com.example.outdoorsy.model.FirebaseModel
import com.example.outdoorsy.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SearchRepository @Inject constructor(private val firebaseModel: FirebaseModel) {


suspend fun getUsersByName(query: String): List<UserModel> {
    return firebaseModel.getUsersByName(query)
}

}

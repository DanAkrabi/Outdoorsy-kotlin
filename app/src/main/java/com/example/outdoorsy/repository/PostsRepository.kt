package com.example.outdoorsy.repository

import com.example.outdoorsy.model.dao.PostModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getUserPosts(userId: String): List<PostModel> {
        return try {
            val querySnapshot = firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                document.toObject(PostModel::class.java)?.copy(postId = document.id)
            }
        } catch (e: Exception) {
            emptyList() // Return an empty list if an error occurs
        }
    }
    suspend fun fetchHomepagePosts(): List<PostModel> {
        return try {
            val querySnapshot = firestore.collection("posts")
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                document.toObject(PostModel::class.java)?.copy(postId = document.id)
            }
        } catch (e: Exception) {
            emptyList() // Return an empty list if an error occurs
        }
    }
    suspend fun searchPostsByQuery(query: String): List<PostModel> {
        // Implement Firestore search here
        // Example:
        return FirebaseFirestore.getInstance()
            .collection("posts")
            .whereEqualTo("fieldname", query) // Adjust based on your data model
            .get()
            .await()
            .toObjects(PostModel::class.java)
    }

}


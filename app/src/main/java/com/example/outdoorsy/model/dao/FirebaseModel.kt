


package com.example.outdoorsy.model.dao

import android.util.Log
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
//FirebaseModel class - similar to Routing

class FirebaseModel(private val firestore: FirebaseFirestore) {
    private val database= Firebase.firestore//initializing a firebase instance
    private val usersCollection = firestore.collection("users")

    init{
        val settings= firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {  })
        }
    }

     suspend fun saveUser(user: UserModel): Boolean {
        return try {
            val json = mapOf(
                "email" to user.email,
                "fullname" to user.fullname,
                "password" to user.password,
                "id" to user.id
            )
            usersCollection.document(user.id).set(json).await()
            Log.d("FirebaseModel", "User added with ID: ${user.id}")
            true
        } catch (e: Exception) {
            Log.e("FirebaseModel", "Error adding user", e)
            false
        }
    }

    // Get user from Firestore
//    suspend fun getUser(userId: String): UserModel? {
//        val documentSnapshot = usersCollection.document(userId).get().await()
//        return documentSnapshot.toObject(UserModel::class.java)
//    }

    // Update user in Firestore
    suspend fun updateUser(userId: String, updatedData: Map<String, Any>) {
        usersCollection.document(userId).update(updatedData).await()
    }

    // Delete user from Firestore
    suspend fun deleteUser(userId: String) {
        usersCollection.document(userId).delete().await()
    }

    suspend fun getUser(userId: String): UserModel? {
        return try {
            val documentSnapshot = usersCollection.document(userId).get().await()
            documentSnapshot.toObject(UserModel::class.java)
        } catch (e: Exception) {
            null // Handle errors as needed
        }
    }

}


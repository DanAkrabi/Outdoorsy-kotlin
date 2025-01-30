
package com.example.outdoorsy.model.dao

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.outdoorsy.viewmodel.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class FirebaseModel @Inject constructor(
    private val firestore: FirebaseFirestore,
//    private val userViewModel: UserViewModel
) {
    private val database = Firebase.firestore // Initializing a firebase instance
    private val usersCollection = firestore.collection("users")
    private val storage = Firebase.storage

    init {
        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings { })
        }
    }

    // Save user to Firestore
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
            Log.e("FirebaseModel", "Error fetching user", e)
            null
        }
    }


//    }

    // Upload image to Firebase Storage
    fun uploadImage(image: Bitmap, name: String, callback: (String?) -> Unit) {
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/$name.jpg")

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            callback(null) // TODO - still need to implement this
        }.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                callback(uri.toString())
            }
        }
    }
}

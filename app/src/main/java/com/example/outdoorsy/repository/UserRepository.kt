package com.example.outdoorsy.repository

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.dao.UserModel
import com.example.outdoorsy.model.dao.FirebaseModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class UserRepository @Inject constructor(
    private val firebaseModel: FirebaseModel,
//    private val firestore: FirebaseFirestore
) {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getFollowersCount(userId: String): Int {
        return try {
            val followersSnapshot = db.collection("users")
                .document(userId)
                .collection("followers")
                .get()
                .await()

            var totalFollowers = 0
            for (document in followersSnapshot.documents) {
                val followerIds = document.get("followerId") as? List<String>
                totalFollowers += followerIds?.size ?: 0
            }

            totalFollowers
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching followers: ${e.message}")
            0
        }
    }

    suspend fun getFollowingCount(userId: String): Int {
        return try {
            val followingSnapshot = db.collection("users")
                .document(userId)
                .collection("following") // Access the "following" subcollection
                .get()
                .await()

            var totalFollowing = 0
            for (document in followingSnapshot.documents) {
                val followingIds = document.get("followingId") as? List<String> // Get the "followingId" array
                totalFollowing += followingIds?.size ?: 0
            }

            totalFollowing
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching following: ${e.message}")
            0
        }
    }
    suspend fun getUserById(userId: String): UserModel? {
        return try {
            val documentSnapshot = db.collection("users")
                .document(userId)
                .get()
                .await()

            documentSnapshot.toObject(UserModel::class.java)?.also {
                it.id = documentSnapshot.id // Set the user ID explicitly
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user by ID: ${e.message}")
            null
        }
    }


    suspend fun fetchUser(userId: String): UserModel? {
        return firebaseModel.getUser(userId)
    }


}


//
//package com.example.outdoorsy.repository
//
//import com.example.outdoorsy.model.dao.FirebaseModel
//import com.example.outdoorsy.model.dao.UserModel
//
//class UserRepository(private val firebaseModel: FirebaseModel) {
//    fun registerUser(email: String, password: String): Boolean {
//        // Implement registration logic, possibly using Firebase or another backend service
//        return true
//    }
////    init {
////        // Automatically add some sample students
////        Users.add(Users(1,"dan@gmail.com","DanAkrabi","1234567"))
////        Users.add(Users(2,"danSalem@gmail.com","DanSalem","17547"))
////        Users.add(Users(3,"danMano@gmail.com","DanMano","127547"))
////
////    }
//
//
//    suspend fun createUser(user: UserModel) {
//       firebaseModel.saveUser(user)
//    }
//
//    suspend fun fetchUser(userId: String): UserModel? {
//        return firebaseModel.getUser(userId)
//    }
//
//    suspend fun modifyUser(userId: String, updatedData: Map<String, Any>) {
//        firebaseModel.updateUser(userId, updatedData)
//    }
//
//    suspend fun removeUser(userId: String) {
//        firebaseModel.deleteUser(userId)
//    }
//
////    fun getFullnameByEmailAndPassword(email: String, password: String): String? {
////        return Users.firstOrNull { it.email == email && it.password == password }?.fullName
////    }
//}

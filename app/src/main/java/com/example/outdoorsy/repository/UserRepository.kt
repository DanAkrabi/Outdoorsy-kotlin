package com.example.outdoorsy.repository

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.dao.UserModel
import com.example.outdoorsy.model.dao.FirebaseModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class UserRepository @Inject constructor(
    private val firebaseModel: FirebaseModel,
//    private val firestore: FirebaseFirestore
) {

    private val db = FirebaseFirestore.getInstance()

//    suspend fun getFollowersCount(userId: String): Int {
//        return try {
//            val followersSnapshot = db.collection("users")
//                .document(userId)
//                .collection("followers")
//                .get()
//                .await()
//
//            followersSnapshot.size()
//        } catch (e: Exception) {
//            Log.e("Firestore", "Error fetching followers: ${e.message}")
//            0
//        }
//    }
//
//    suspend fun getFollowingCount(userId: String): Int {
//        return try {
//            val followingSnapshot = db.collection("users")
//                .document(userId)
//                .collection("following")
//                .get()
//                .await()
//
//            followingSnapshot.size()
//        } catch (e: Exception) {
//            Log.e("Firestore", "Error fetching following: ${e.message}")
//            0
//        }
//    }

    suspend fun getFollowersCount(userId: String): Int {
        return try {
            val userDocument = db.collection("users").document(userId).get().await()
            userDocument.getLong("followersCount")?.toInt() ?: 0
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching followers count: ${e.message}")
            0
        }
    }

    // Fetch the following count from the user's document
    suspend fun getFollowingCount(userId: String): Int {
        return try {
            val userDocument = db.collection("users").document(userId).get().await()
            userDocument.getLong("followingCount")?.toInt() ?: 0
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching following count: ${e.message}")
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
    suspend fun toggleFollowUser(profileUserId: String): Boolean {
        val loggedInUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return false
        return try {
            var didFollow = false
            db.runTransaction { transaction ->
                val loggedInUserRef = db.collection("users").document(loggedInUserId)
                val profileUserRef = db.collection("users").document(profileUserId)
                val loggedInUserFollowingRef = loggedInUserRef.collection("following").document(profileUserId)
                val profileUserFollowersRef = profileUserRef.collection("followers").document(loggedInUserId)

                // Check if already following
                val snapshot = transaction.get(loggedInUserFollowingRef)
                if (snapshot.exists()) {
                    // Unfollow
                    transaction.delete(loggedInUserFollowingRef)
                    transaction.delete(profileUserFollowersRef)
                    transaction.update(loggedInUserRef, "followingCount", FieldValue.increment(-1))
                    transaction.update(profileUserRef, "followersCount", FieldValue.increment(-1))
                } else {
                    // Follow
                    transaction.set(loggedInUserFollowingRef, hashMapOf("timestamp" to FieldValue.serverTimestamp()))
                    transaction.set(profileUserFollowersRef, hashMapOf("timestamp" to FieldValue.serverTimestamp()))
                    transaction.update(loggedInUserRef, "followingCount", FieldValue.increment(1))
                    transaction.update(profileUserRef, "followersCount", FieldValue.increment(1))
                    didFollow = true
                }
            }.await()
            didFollow
        } catch (e: Exception) {
            Log.e("UserRepository", "Error toggling follow: ${e.localizedMessage}")
            false
        }
    }

    suspend fun isFollowing(loggedInUserId: String, profileUserId: String): Boolean {
        return try {
            val loggedInUserFollowingRef = db.collection("users").document(loggedInUserId).collection("following").document(profileUserId)
            val snapshot = loggedInUserFollowingRef.get().await()
            snapshot.exists() // Returns true if the document exists, false otherwise
        } catch (e: Exception) {
            Log.e("UserRepository", "Error checking if following: ${e.message}")
            false // Assume not following if there's an error
        }
    }

    suspend fun followUser(loggedInUserId: String, profileUserId: String) {
        updateCounts(loggedInUserId, profileUserId, 1)
    }

    suspend fun unfollowUser(loggedInUserId: String, profileUserId: String) {
        updateCounts(loggedInUserId, profileUserId, -1)
    }

    private fun updateCounts(loggedInUserId: String, profileUserId: String, increment: Int) {
        db.runTransaction { transaction ->
            val profileUserRef = db.collection("users").document(profileUserId)
            val loggedInUserRef = db.collection("users").document(loggedInUserId)

            val profileUser = transaction.get(profileUserRef)
            val loggedInUser = transaction.get(loggedInUserRef)

            val profileFollowersCount = (profileUser.getLong("followersCount") ?: 0) + increment
            val loggedInFollowingCount = (loggedInUser.getLong("followingCount") ?: 0) + increment

            transaction.update(profileUserRef, "followersCount", profileFollowersCount)
            transaction.update(loggedInUserRef, "followingCount", loggedInFollowingCount)
        }
    }

    suspend fun fetchUser(userId: String): UserModel? {
        return firebaseModel.getUser(userId)
    }



}



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



    suspend fun getFollowersCount(userId: String): Int {
       return firebaseModel.getFollowersCount(userId)
    }

    // Fetch the following count from the user's document
    suspend fun getFollowingCount(userId: String): Int {
   return firebaseModel.getFollowersCount(userId)
    }

//    suspend fun fetchUserById(userId: String): UserModel? {
//      return firebaseModel.fetchUserById(userId)
//    }
    suspend fun getUserById(userId: String): UserModel? {
       return firebaseModel.getUserById(userId)
    }
    suspend fun toggleFollowUser(profileUserId: String): Boolean {
       return firebaseModel.toggleFollowUser(profileUserId)
    }

    suspend fun isFollowing(loggedInUserId: String, profileUserId: String): Boolean {
        return firebaseModel.isFollowing(loggedInUserId,profileUserId)
    }

    suspend fun followUser(loggedInUserId: String, profileUserId: String) {
        firebaseModel.followUser(loggedInUserId, profileUserId)
    }

    suspend fun unfollowUser(loggedInUserId: String, profileUserId: String) {
        firebaseModel.unfollowUser(loggedInUserId, profileUserId)
    }



    suspend fun fetchUser(userId: String): UserModel? {
        return firebaseModel.getUser(userId)
    }



}



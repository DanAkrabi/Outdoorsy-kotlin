package com.example.outdoorsy.repository

import android.net.Uri
import com.example.outdoorsy.model.CloudinaryModel
import com.example.outdoorsy.model.FirebaseModel
import com.example.outdoorsy.model.UserModel
import com.example.outdoorsy.model.dao.UserDao
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject


class UserRepository @Inject constructor(
    private val firebaseModel: FirebaseModel,
//    private val firestore: FirebaseFirestore
    private val userDao: UserDao,
    private val cloudinaryModel: CloudinaryModel
) {

    private val db = FirebaseFirestore.getInstance()

//    fun updateUserProfile(fullName: String, newImageUrl: String?,onSuccess: () -> Unit) {
//        firebaseModel.updateUserProfile(fullName,newImageUrl,onSuccess)
//    }

    fun updateUserProfile(
        fullName: String,
        newImageUrl: String?,
        currentImageUrl: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Logic to update Firebase and/or Cloudinary
        firebaseModel.updateUserProfile(fullName, newImageUrl, currentImageUrl, onSuccess, onError)
    }

    suspend fun getFollowersCount(userId: String): Int {
       return firebaseModel.getFollowersCount(userId)
    }

    // Fetch the following count from the user's document
    suspend fun getFollowingCount(userId: String): Int {
   return firebaseModel.getFollowingCount(userId)
    }


//    suspend fun getUserById(userId: String): UserModel? {
//       return firebaseModel.getUserById(userId)
//    }
suspend fun getUserById(userId: String): UserModel? {
    // ✅ 1. Check if user exists in Room (local storage)
    val cachedUser = userDao.getUserByIdSync(userId)
    if (cachedUser != null) {
        return cachedUser
    }

    // ✅ 2. If no local data, fetch from Firebase
    val firebaseUser = firebaseModel.getUserById(userId)

    // ✅ 3. Store in Room for offline use
    if (firebaseUser != null) {
        userDao.insertUser(firebaseUser)
    }

    return firebaseUser
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



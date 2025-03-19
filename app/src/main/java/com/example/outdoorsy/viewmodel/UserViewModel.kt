package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.outdoorsy.model.UserModel
import com.example.outdoorsy.model.dao.UserDao
import kotlinx.coroutines.launch
import com.example.outdoorsy.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userDao: UserDao
) : ViewModel() {

    val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> get() = _user

    private val _followersCount = MutableLiveData<Int>()
    val followersCount: LiveData<Int> get() = _followersCount

    private val _followingCount = MutableLiveData<Int>()
    val followingCount: LiveData<Int> get() = _followingCount

    private val _isFollowing = MutableLiveData<Boolean>()
    val isFollowing: LiveData<Boolean> get() = _isFollowing

    private val auth = FirebaseAuth.getInstance()
    private val loggedInUserId = auth.currentUser?.uid



val userLiveData: LiveData<UserModel> = userDao.getUserLiveData(loggedInUserId!!) // Observe local data



    // Fetch user by ID and update LiveData

    fun fetchUser(userId: String) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            if (user != null) {
                _user.postValue(user)
                checkIfFollowing(userId)
            } else {
                Log.e("UserViewModel", "Failed to fetch user")
            }
        }
    }

    private fun checkIfFollowing(profileUserId: String) {
        loggedInUserId?.let { currentUserId ->
            viewModelScope.launch {
                val isFollowing = userRepository.isFollowing(currentUserId, profileUserId)
                _isFollowing.postValue(isFollowing)  // ✅ Updates LiveData
            }
        }
    }

fun followButtonClicked(profileUserId: String) {
    viewModelScope.launch {
        val isFollowing = userRepository.toggleFollowUser(profileUserId)
        _isFollowing.postValue(isFollowing)

        // Fetch the latest counts to update the UI accurately
        updateCounts(profileUserId)
    }
}

    private fun updateCounts(profileUserId: String) {
        viewModelScope.launch {
            _followersCount.postValue(userRepository.getFollowersCount(profileUserId))
            _followingCount.postValue(userRepository.getFollowingCount(FirebaseAuth.getInstance().currentUser?.uid ?: ""))
        }
    }




    fun setUser(user: UserModel?) {
        if (user != null) {
            _user.value = user
            Log.d("UserViewModel", "User set: ${user.fullname}")
        } else {
            Log.e("UserViewModel", "Attempt to set null user")
        }
    }

    // Fetch and update followers count
    fun fetchFollowersCount(userId: String) {
        viewModelScope.launch {
            try {
                val count = userRepository.getFollowersCount(userId)
                _followersCount.postValue(count)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching followers count: ${e.message}")
            }
        }
    }

    // Fetch and update following count
    fun fetchFollowingCount(userId: String) {
        viewModelScope.launch {
            try {
                val count = userRepository.getFollowingCount(userId)
                _followingCount.postValue(count)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching following count: ${e.message}")
            }
        }
    }

//    fun getUserDetails(userId: String, callback: (String?, String?) -> Unit) {
//        viewModelScope.launch {
//            Log.d("UserViewModel", "Fetching details for userID: $userId")
//            try {
//                val user = userRepository.getUserById(userId)
//                if (user != null) {
//                    Log.d("UserViewModel", "User found: ${user.fullname}")
//                    callback(user.fullname, user.profileImg)
//                } else {
//                    Log.d("UserViewModel", "No user found for ID: $userId")
//                    callback(null, null)
//                }
//            } catch (e: Exception) {
//                Log.e("UserViewModel", "Error fetching user details: ${e.message}", e)
//                callback(null, null)
//            }
//        }
//    }


    fun getUserDetails(userId: String, callback: (String?, String?) -> Unit) {
        viewModelScope.launch {
            // Step 1: Try to load from Room (SQLite)
            val cachedUser = userRepository.getUserById(userId)
            if (cachedUser != null) {
                Log.d("UserViewModel", "✅ Loaded user from Room: ${cachedUser.fullname}")
                callback(cachedUser.fullname, cachedUser.profileImg)
                return@launch // ✅ Stop here, no need to fetch from Firebase
            }

            // Step 2: If not found in Room, fetch from Firebase
            Log.d("UserViewModel", "⏳ No cache found, fetching from Firebase for userID: $userId")
            try {
                val user = userRepository.getUserById(userId)
                if (user != null) {
                    Log.d("UserViewModel", "✅ User fetched from Firebase: ${user.fullname}")

                    // Step 3: Store in Room for next time
                    userDao.insertUser(user)
                    Log.d("UserViewModel", "✅ Stored user in Room for caching")

                    callback(user.fullname, user.profileImg) // ✅ Update UI with new data
                } else {
                    Log.e("UserViewModel", "🚨 No user found in Firebase")
                    callback(null, null)
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "❌ Error fetching user from Firebase: ${e.message}", e)
                callback(null, null)
            }
        }
    }

    fun getUserLiveData(userId: String): LiveData<UserModel> {
        Log.d("RoomDebug", "Fetching user from Room DB: $userId")
        return userDao.getUserLiveData(userId) // ✅ Observe user changes in Room
    }

    fun refreshUserData(userId: String) {
        viewModelScope.launch {
            val cachedUser = userRepository.getUserById(userId) // ✅ Uses repository (checks cache + Firebase)
            if (cachedUser != null) {
                Log.d("CacheDebug", "✅ Loaded user from Room cache: ${cachedUser.fullname}")
                _user.postValue(cachedUser) // ✅ Update UI
            } else {

                Log.e("UserViewModel", "Failed to fetch user")
            }
        }
    }

}


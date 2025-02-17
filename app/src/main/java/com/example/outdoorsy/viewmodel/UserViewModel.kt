package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.outdoorsy.model.UserModel
import kotlinx.coroutines.launch
import com.example.outdoorsy.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
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
//    fun getUserDetails(userId: String) {
//        viewModelScope.launch {
//            val userDetails = userRepository.getUserById(userId)
//            _user.postValue(userDetails)
//        }
//    }
//    fun getUserDetails(userId: String, callback: (String?, String?) -> Unit) {
//        viewModelScope.launch {
//            try {
//                val user = userRepository.getUserById(userId)
//                callback(user?.fullname, user?.profileImg)
//            } catch (e: Exception) {
//                callback(null, null)
//            }
//        }
//    }
fun getUserDetails(userId: String, callback: (String?, String?) -> Unit) {
    viewModelScope.launch {
        Log.d("UserViewModel", "Fetching details for userID: $userId")
        try {
            val user = userRepository.getUserById(userId)
            if (user != null) {
                Log.d("UserViewModel", "User found: ${user.fullname}")
                callback(user.fullname, user.profileImg)
            } else {
                Log.d("UserViewModel", "No user found for ID: $userId")
                callback(null, null)
            }
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error fetching user details: ${e.message}", e)
            callback(null, null)
        }
    }
}

    // Fetch user by ID and update LiveData
//    fun fetchUser(userId: String) {
//        viewModelScope.launch {
//            userRepository.getUserById(userId)?.let {
//                _user.postValue(it)
//
//                checkIfFollowing(userId)
//            } ?: Log.e("UserViewModel", "Failed to fetch user")
//        }
//    }
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


}


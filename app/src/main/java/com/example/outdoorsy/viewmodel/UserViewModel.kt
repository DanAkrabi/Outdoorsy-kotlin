package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import com.example.outdoorsy.model.dao.UserModel
import com.example.outdoorsy.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firestore: FirebaseFirestore
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

    // Fetch user by ID and update LiveData
    fun fetchUser(userId: String) {
        viewModelScope.launch {
            userRepository.getUserById(userId)?.let {
                _user.postValue(it)

                checkIfFollowing(userId)
            } ?: Log.e("UserViewModel", "Failed to fetch user")
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


package com.example.outdoorsy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.dao.UserModel
import com.example.outdoorsy.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _userProfile = MutableLiveData<UserModel?>()
    val userProfile: LiveData<UserModel?> = _userProfile

    private val _followersCount = MutableLiveData<Int>()
    val followersCount: LiveData<Int> = _followersCount

    private val _followingCount = MutableLiveData<Int>()
    val followingCount: LiveData<Int> = _followingCount

    private val _isFollowing = MutableLiveData<Boolean>()
    val isFollowing: LiveData<Boolean> = _isFollowing

    fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            userRepository.getUserById(userId)?.let { user ->
                _userProfile.postValue(user)
                _followersCount.postValue(userRepository.getFollowersCount(userId))
                _followingCount.postValue(userRepository.getFollowingCount(userId))
                checkIfFollowing(userId)
            }
        }
    }

    private suspend fun checkIfFollowing(profileUserId: String) {
        val loggedInUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val isFollowing = userRepository.isFollowing(loggedInUserId, profileUserId)
        _isFollowing.postValue(isFollowing)
    }
    fun toggleFollowUser(profileUserId: String) {
        viewModelScope.launch {
            val result = userRepository.toggleFollowUser(profileUserId)
            _isFollowing.postValue(result)
            // Optionally refresh follower/following counts
            fetchUserProfile(profileUserId)
        }
    }
}

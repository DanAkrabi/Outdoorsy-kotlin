package com.example.outdoorsy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.PostModel
import com.example.outdoorsy.model.UserModel
import com.example.outdoorsy.repository.PostRepository
import com.example.outdoorsy.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
//    private val userViewModel: UserViewModel,

) : ViewModel() {

    private val _posts = MutableLiveData<List<PostModel>>()
    val posts: LiveData<List<PostModel>> = _posts

    private val _followersCount = MutableLiveData<Int>()
    val followersCount: LiveData<Int> = _followersCount

    private val _followingCount = MutableLiveData<Int>()
    val followingCount: LiveData<Int> = _followingCount

    private val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> = _user

    fun fetchUserData(userId: String) {
        viewModelScope.launch {
            _user.postValue(userRepository.getUserById(userId)) // Assuming getUser returns LiveData
        }
    }


fun fetchUserPosts(userId: String) {
    viewModelScope.launch {
        val posts = postRepository.getUserPosts(userId)
        val sortedPosts = posts.sortedByDescending { it.timestamp } // ✅ Sort newest first
        _posts.postValue(sortedPosts) // ✅ Update LiveData with sorted posts
    }
}

    fun fetchFollowersAndFollowingCounts(userId: String) {
        viewModelScope.launch {
            _followersCount.postValue(userRepository.getFollowersCount(userId))
            _followingCount.postValue(userRepository.getFollowingCount(userId))
        }
    }
}


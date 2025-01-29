package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.dao.PostModel
import com.example.outdoorsy.model.dao.UserModel
import com.example.outdoorsy.repository.PostRepository
import com.example.outdoorsy.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
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
            _posts.postValue(postRepository.getUserPosts(userId))
        }
    }

    fun fetchFollowersAndFollowingCounts(userId: String) {
        viewModelScope.launch {
            _followersCount.postValue(userRepository.getFollowersCount(userId))
            _followingCount.postValue(userRepository.getFollowingCount(userId))
        }
    }
}


//package com.example.outdoorsy.viewmodel
//
//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.outdoorsy.model.dao.PostModel
//import com.example.outdoorsy.model.dao.UserModel
//import com.example.outdoorsy.repository.PostRepository
//import com.example.outdoorsy.repository.UserRepository
//import com.google.firebase.firestore.Query
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//import javax.inject.Inject
//
//@HiltViewModel
//class ProfileViewModel @Inject constructor(private val userRepository: UserRepository,
//                                           private val postRepository: PostRepository
//): ViewModel() {
//    private val _posts = MutableLiveData<List<PostModel>>()
//    val posts: LiveData<List<PostModel>> get() = _posts
//
//    private val _followersCount = MutableLiveData<Int>()
//    val followersCount: LiveData<Int> get() = _followersCount
//
//    private val _followingCount = MutableLiveData<Int>()
//    val followingCount: LiveData<Int> get() = _followingCount
//
//    private val _user = MutableLiveData<UserModel?>()
//    val user: MutableLiveData<UserModel?> get() = _user
//
//    fun fetchFollowersAndFollowing(userId: String) {
//        // Fetch followers and following counts
//        fetchFollowersCount(userId)
//        fetchFollowingCount(userId)
//    }
//
//
//
//    private fun fetchFollowersCount(userId: String) {
//        viewModelScope.launch {
//            try {
//                val followersCount = userRepository.getFollowersCount(userId)
//                _followersCount.postValue(followersCount)
//            } catch (e: Exception) {
//                Log.e("ProfileViewModel", "Error fetching followers count: ${e.message}")
//            }
//        }
//    }
//
//    private fun fetchFollowingCount(userId: String) {
//        viewModelScope.launch {
//            try {
//                val followingCount = userRepository.getFollowingCount(userId)
//                _followingCount.postValue(followingCount)
//            } catch (e: Exception) {
//                Log.e("ProfileViewModel", "Error fetching following count: ${e.message}")
//            }
//        }
//    }
//    fun fetchUserPosts(userId: String) {
//        // Fetch user posts
//        viewModelScope.launch {
//            try {
//                val posts = postRepository.getUserPosts(userId) // Modify to fetch posts for specific user
//                _posts.postValue(posts)
//            } catch (e: Exception) {
//                Log.e("ProfileViewModel", "Error fetching posts: ${e.message}")
//            }
//        }
//    }
//}
//

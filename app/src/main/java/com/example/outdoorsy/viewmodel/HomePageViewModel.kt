package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.adapters.Destination
import com.example.outdoorsy.model.dao.PostModel
import com.example.outdoorsy.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class HomepageViewModel @Inject constructor(private val postRepository: PostRepository) : ViewModel() {
    // Backing property for destinations
//    private val _destinations = MutableLiveData<List<Destination>>()
//    val destinations: LiveData<List<Destination>> get() = _destinations
//
//    // Backing property for error messages
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // Fetch destinations asynchronously
    private val _posts = MutableLiveData<List<PostModel>>()
    val posts: LiveData<List<PostModel>> get() = _posts

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading
//    fun fetchHomepagePosts() {
//        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
//
//        viewModelScope.launch {
//            val posts = postRepository.getFeedPosts(currentUserId)
//            _posts.postValue(posts) // ✅ Update LiveData
//        }
//    }
//    fun fetchHomepagePosts() {
//        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
//
//        viewModelScope.launch {
//            _isLoading.postValue(true)
//            val posts = postRepository.getFeedPosts(currentUserId) // ✅ Fetch posts
//            _posts.postValue(posts) // ✅ Update UI
//            _isLoading.postValue(false)
//        }
//    }
//fun fetchHomepagePosts() {
//    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
//
//    viewModelScope.launch {
//        _isLoading.postValue(true)
//
//        val posts = postRepository.getFeedPosts(currentUserId) // ✅ Fetch posts
//        Log.d("HomepageViewModel", "Fetched posts: ${posts.size}") // 🔴 Log fetched posts
//
//        _posts.postValue(posts) // ✅ Update UI
//        _isLoading.postValue(false)
//    }
//}

    fun fetchHomepagePosts() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            _isLoading.postValue(true)

            val posts = postRepository.getFeedPosts(currentUserId) // ✅ Fetch posts
            Log.d("HomepageViewModel", "Fetched posts count: ${posts.size}") // 🔴 Log post count

            if (posts.isEmpty()) {
                Log.e("HomepageViewModel", "No posts found for user feed!")
            } else {
                for (post in posts) {
                    Log.d("HomepageViewModel", "Post: ${post.postId} - ${post.textContent}")
                }
            }

            _posts.postValue(posts) // ✅ Update UI
            _isLoading.postValue(false)
        }
    }


//    fun fetchHomepagePosts() {   ORIGINAL ONE
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val posts = postRepository.fetchHomepagePosts()
//                _posts.value = posts
//            } catch (e: Exception) {
//                // Handle error
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }

//    fun searchPosts(query: String) {
//        viewModelScope.launch {
//            val filteredPosts = postRepository.searchPostsByQuery(query) // Implement this in your repository
//            _posts.value = filteredPosts
//        }
//    }

}


package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.adapters.Destination
import com.example.outdoorsy.model.PostModel
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
    val posts: LiveData<List<PostModel>> = postRepository.getLocalPosts()
    // Fetch destinations asynchronously
    private var _posts = MutableLiveData<List<PostModel>>()
//    val posts: LiveData<List<PostModel>> get() = _posts
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading


    fun fetchHomepagePosts() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserId == null) {
            Log.e("HomepageViewModel", "User not logged in!")
            _error.postValue("User not logged in!")
            return
        }

        viewModelScope.launch {
            _isLoading.postValue(true)

            try {
                postRepository.getFeedPosts(currentUserId) // ✅ Fetch & update Room
            } catch (e: Exception) {
                Log.e("HomepageViewModel", "Error fetching posts: ${e.message}")
                _error.postValue(e.message)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }






}


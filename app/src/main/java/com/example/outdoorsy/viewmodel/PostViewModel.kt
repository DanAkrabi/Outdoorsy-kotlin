package com.example.outdoorsy.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.CommentModel
import com.example.outdoorsy.model.PostModel
import com.example.outdoorsy.model.dao.PostDao

import com.example.outdoorsy.repository.CameraRepository
import com.example.outdoorsy.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val cameraRepository: CameraRepository
) : ViewModel() {



    private val _posts = MutableLiveData<List<PostModel>>()
//    val posts: LiveData<List<PostModel>> get() = _posts
val posts: LiveData<List<PostModel>> = postRepository.getLocalPosts()

    private val _post = MutableLiveData<PostModel>()
    val post: LiveData<PostModel> get() = _post

    private val _comments = MutableLiveData<List<CommentModel>>()
    val comments: LiveData<List<CommentModel>> get() = _comments


    private val _likesCount = MutableLiveData<Long>()
    val likesCount: LiveData<Long> get() = _likesCount

    private val _feedPosts = MutableLiveData<List<PostModel>>()
    val feedPosts: LiveData<List<PostModel>> get() = _feedPosts

    private val _isLikedByUser = MutableLiveData<Boolean>()
    val isLikedByUser: LiveData<Boolean> get() = _isLikedByUser

    // Fetch posts for a user
    fun fetchUserPosts(userId: String) {
        viewModelScope.launch {
            val posts = postRepository.getUserPosts(userId)
            _posts.postValue(posts)
        }
    }

    // Fetch a single post's details, including likes and comments
    fun fetchPostDetails(postId: String) {
        viewModelScope.launch {
            val post = postRepository.getPostById(postId)
            Log.d("PostViewModel", "Fetched post details: $post")
            _post.postValue(post)

            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

            // Check if the current user has liked this post
            val isLiked = postRepository.isLikedByUser(postId, currentUserId)
            _isLikedByUser.postValue(isLiked)

            // Fetch likes count
            postRepository.getPostLikesCount(postId) { likes ->
                _likesCount.postValue(likes)
            }
        }
    }



    // Fetch comments for a specific post
    private fun fetchCommentsForPost(postId: String) {
        viewModelScope.launch {
            val comments = postRepository.getCommentsForPost(postId)
            _comments.postValue(comments)
        }
    }


    fun addCommentToPost(postId: String, comment: CommentModel) {
        viewModelScope.launch {
            val newCommentCount = postRepository.addCommentToPost(postId, comment)

            if (newCommentCount != -1) { // Ensure no error occurred
                _post.value?.let { updatedPost ->
                    _post.postValue(updatedPost.copy(commentsCount = newCommentCount))
                }
            }
        }
    }


fun fetchPostLikesCount(postId: String) {
    viewModelScope.launch {
        postRepository.fetchPostLikesCount(postId) { likes ->
            _likesCount.postValue(likes)
        }
    }
}

    // Observe likes count changes continuously
    fun observeLikesCount(postId: String) {
        postRepository.observePostLikesCount(postId) { likes ->
            _likesCount.postValue(likes)
        }
    }

fun toggleLike(postId: String) {
    viewModelScope.launch {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

        // ✅ Store the previous state before toggling
        val isCurrentlyLiked = _isLikedByUser.value ?: false

        // ✅ Perform the Firestore operation
        val success = postRepository.toggleLike(postId, currentUserId)
        val updatedIsLiked = postRepository.isLikedByUser(postId, currentUserId)

        if (success) {
            // ✅ Force an immediate UI update based on the expected outcome
            _isLikedByUser.postValue(!isCurrentlyLiked)

            // ✅ Fetch the actual state from Firestore to confirm the UI is correct
            _isLikedByUser.postValue(updatedIsLiked)

            // ✅ Ensure the likes count is updated correctly
            postRepository.getPostLikesCount(postId) { likes ->
                _likesCount.postValue(likes)
            }
        } else {
            _isLikedByUser.postValue(updatedIsLiked)
            Log.e("FirestoreError", "Toggle like failed")
        }
    }
}

    fun fetchFeedPosts(userId: String) {
        viewModelScope.launch {
            val posts = postRepository.getFeedPosts(userId)
            _feedPosts.postValue(posts) // Update LiveData to reflect UI changes
        }
    }

    fun checkLikeStatus(postId: String, userId: String) {
        viewModelScope.launch {
            val liked = postRepository.isLikedByUser(postId, userId)
            _isLikedByUser.postValue(liked)
        }
    }


    // Delete a post
    fun deletePost(postId: String) {
        viewModelScope.launch {
            postRepository.deletePost(postId)
        }
    }






}



package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.dao.PostModel
import com.example.outdoorsy.model.dao.CommentModel
import com.example.outdoorsy.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _posts = MutableLiveData<List<PostModel>>()
    val posts: LiveData<List<PostModel>> get() = _posts

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



//    fun toggleLike(postId: String, userId: String) {
//        viewModelScope.launch {
//            // Fetch the current like state from the repository
//            val isCurrentlyLiked = postRepository.isLikedByUser(postId, userId)
//
//            // Attempt to toggle the like status in the repository
//            val success = postRepository.toggleLike(postId, userId)
//
//            if (success) {
//                // If the toggle was successful, compute the new like count
//                checkLikeStatus(postId, userId)
////                val newLikesCount = if (isCurrentlyLiked) {
////                    max(0, (_likesCount.value ?: 0) - 1)  // Decrement if it was liked
////                } else {
////                    (_likesCount.value ?: 0) + 1  // Increment if it was not liked
////                }
////
////                // Update the LiveData for likes count
////                _likesCount.value = newLikesCount
////
////                // Update the LiveData for like status
////                _isLikedByUser.value = !isCurrentlyLiked
//                val latestLikedStatus = postRepository.isLikedByUser(postId, userId)
//                _isLikedByUser.postValue(latestLikedStatus)
//                postRepository.getPostLikesCount(postId) { updatedLikes ->
//                    _likesCount.postValue(updatedLikes)
//                }
//            } else {
//                // Handle the error case if needed, for example logging or showing an error message
//            }
//        }
//    }

//    fun toggleLike(postId: String, userId: String) {
//        viewModelScope.launch {
//            val isCurrentlyLiked = postRepository.isLikedByUser(postId, userId)
//
//            // ✅ Perform the Firestore like/unlike operation
//            val success = postRepository.toggleLike(postId, userId)
//
//            if (success) {
//                // ✅ Fetch the actual status from Firestore instead of flipping the boolean
//                val latestLikedStatus = postRepository.isLikedByUser(postId, userId)
//                _isLikedByUser.postValue(latestLikedStatus)
//
//                // ✅ Ensure the likes count is updated
//                postRepository.getPostLikesCount(postId) { updatedLikes ->
//                    _likesCount.postValue(updatedLikes)
//                }
//            } else {
//                Log.e("FirestoreError", "Toggle like failed")
//            }
//        }
//    }

//    fun toggleLike(postId: String) {
//        viewModelScope.launch {
//            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
//
//            val isCurrentlyLiked = _isLikedByUser.value ?: false
//            val success = postRepository.toggleLike(postId, currentUserId)
//
//            if (success) {
//                // After toggling, re-check the actual status from Firestore
//                val updatedIsLiked = postRepository.isLikedByUser(postId, currentUserId)
//                _isLikedByUser.postValue(updatedIsLiked)
//
//                // Update likes count based on real Firestore data
//                postRepository.getPostLikesCount(postId) { likes ->
//                    _likesCount.postValue(likes)
//                }
//            }
//        }
//    }
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


}



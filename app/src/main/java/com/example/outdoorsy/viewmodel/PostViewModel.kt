package com.example.outdoorsy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.dao.PostModel
import com.example.outdoorsy.model.dao.CommentModel
import com.example.outdoorsy.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

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
//            fetchCommentsForPost(postId)
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
            postRepository.addCommentToPost(postId, comment)
            // Re-fetch the post details to update the LiveData observed by the UI
            fetchPostDetails(postId)
        }
    }
    // Handle liking a post (increment or decrement like count)
    fun toggleLike(postId: String) {
        viewModelScope.launch {
            val updatedPost = postRepository.toggleLike(postId)
            _post.postValue(updatedPost)
        }
    }


}



package com.example.outdoorsy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.dao.CommentModel
import com.example.outdoorsy.repository.CommentsRepository
import com.example.outdoorsy.repository.PostRepository
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(private val commentsRepository: CommentsRepository,private val postRepository: PostRepository) : ViewModel() {

    private val _comments = MutableLiveData<List<CommentModel>>()
    val comments: LiveData<List<CommentModel>> = _comments

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    private val _commentCount = MutableLiveData<Int>()
    val commentCount: LiveData<Int> get() = _commentCount

fun fetchComments(postId: String) {
    viewModelScope.launch {
        try {
//            _comments.value = commentsRepository.getCommentsForPost(postId)
        val commentList=commentsRepository.getCommentsForPost(postId)

        _comments.postValue(commentList)
        } catch (e: Exception) {
            _errorMessage.value = e.message ?: "An error occurred"
        }
    }
}



}

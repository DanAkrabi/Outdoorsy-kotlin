package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.CommentModel
import com.example.outdoorsy.repository.CommentsRepository
import com.example.outdoorsy.repository.PostRepository
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val commentsRepository: CommentsRepository
) : ViewModel() {
    private val _comments = MutableLiveData<List<CommentModel>>()
    val comments: LiveData<List<CommentModel>> get() = _comments

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchComments(postId: String) {
        Log.d("Comments", "🟢 Fetching comments for postId: $postId")
        viewModelScope.launch {
            try {
                commentsRepository.refreshCommentsForPost(postId) // 🔥 Fetch from Firebase & save to Room

                commentsRepository.getLocalComments(postId).observeForever { localComments ->
                    Log.d("Comments", "✅ ViewModel received ${localComments.size} comments from Room for postId=$postId")
                    _comments.postValue(localComments)
                }

            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "An error occurred")
                Log.e("Comments", "❌ Error fetching comments: ${e.message}")
            }
        }
    }


    fun clearAllComments() {
        viewModelScope.launch {
            commentsRepository.clearAllComments()
            _comments.postValue(emptyList())
        }
    }
}


//@HiltViewModel
//class CommentsViewModel @Inject constructor(
//    private val commentsRepository: CommentsRepository,
//    private val postRepository: PostRepository
//) : ViewModel() {
//
//    private val _comments = MutableLiveData<List<CommentModel>>()
//    val comments: LiveData<List<CommentModel>> = _comments
//
//    private val _errorMessage = MutableLiveData<String>()
//    val errorMessage: LiveData<String> = _errorMessage
//
//    private val _commentCount = MutableLiveData<Int>()
//    val commentCount: LiveData<Int> get() = _commentCount
//
//    fun fetchComments(postId: String) {
//        Log.d("CommentsViewModel", "Fetching comments for postId: $postId")
//        viewModelScope.launch {
//            try {
//                commentsRepository.getCommentsForPost(postId) // 🔥 Fetch from Firestore & save to Room
//
//                // ✅ Observe Room DB (LiveData updates automatically)
//                commentsRepository.getLocalComments(postId).observeForever { localComments ->
//                    Log.d("CommentsViewModel", "Received ${localComments.size} comments from Room")
//                    (comments as MutableLiveData).postValue(localComments)
//                }
//
//            } catch (e: Exception) {
//                _errorMessage.postValue(e.message ?: "An error occurred")
//            }
//        }
//    }
//
//
//    fun clearAllComments() {
//        viewModelScope.launch {
//            commentsRepository.clearAllComments()
//            _comments.postValue(emptyList()) // 🔥 Ensure LiveData is updated
//        }
//    }
//}
//
//
//

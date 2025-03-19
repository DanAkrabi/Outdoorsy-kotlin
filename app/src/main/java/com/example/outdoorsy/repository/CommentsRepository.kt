package com.example.outdoorsy.repository

import android.util.Log
//import androidx.compose.ui.test.filter
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
//import androidx.paging.map
import com.example.outdoorsy.model.CommentModel
import com.example.outdoorsy.model.FirebaseModel
import com.example.outdoorsy.model.dao.CommentDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import javax.inject.Inject
class CommentsRepository @Inject constructor(
    private val firebaseModel: FirebaseModel,
    private val commentDao: CommentDao
) {
    suspend fun refreshCommentsForPost(postId: String) {
        withContext(Dispatchers.IO) {
            try {
                val remoteComments = firebaseModel.getCommentsForPost(postId)
                Log.d("Comments", "Loaded ${remoteComments.size} comments from Firebase")

                // Prepare comments for insertion with updated post IDs
                val commentsWithPostId = remoteComments.map {
                    it.copy(commentId = "$postId-${it.commentId}")
                }

                // Use a transaction to ensure atomicity
                commentDao.runInTransaction {
                    commentDao.clearCommentsForPost(postId)
                    commentDao.insertComments(commentsWithPostId)
                }

                Log.d("Comments", "Saved ${commentsWithPostId.size} comments to Room")
            } catch (e: Exception) {
                Log.e("Comments", "Error refreshing comments: ${e.message}")
            }
        }
    }

//    fun getLocalComments(postId: String): LiveData<List<CommentModel>> {
//
//        return commentDao.getCommentsForPost(postId)
//    }

    fun getLocalComments(postId: String): LiveData<List<CommentModel>> {
        return commentDao.getCommentsForPost(postId).map { comments ->
            Log.d("Comments", "📥 Loaded ${comments.size} comments from Room for postId=$postId")
            comments
        }
    }
    suspend fun clearAllComments() {
        commentDao.clearAllComments()
    }
}

//class CommentsRepository @Inject constructor(private val firebaseModel: FirebaseModel, private val commentDao: CommentDao) {
//
//
//suspend fun saveCommentsToLocalDB(comments: List<CommentModel>) {
//    commentDao.insertComments(comments) // ✅ Save to Room
//}
//
//    suspend fun getCommentsForPost(postId: String): List<CommentModel> {
//        return withContext(Dispatchers.IO) {
//            val remoteComments = firebaseModel.getCommentsForPost(postId)
//            Log.d("CommentsRepository", "Loaded ${remoteComments.size} comments from Firebase") // ✅ Check Firestore count
//
//            // Clear all comments before inserting new ones
//            commentDao.clearAllComments()
//
//            // Add the postId to each comment before saving
//            val commentsWithPostId = remoteComments.map {
//                it.copy(commentId = "$postId-${it.commentId}")
//            }
//            Log.d("CommentsRepository", "Saving ${commentsWithPostId.size} comments to Room...") // ✅ Check Room count
//
//            commentDao.insertComments(commentsWithPostId)  // Ensure all comments are saved
//
//            remoteComments
//        }
//    }
//
//
//
//
//    suspend fun clearAllComments(){
//        commentDao.clearAllComments()
//    }
//
//
//    fun getLocalComments(postId: String): LiveData<List<CommentModel>> {
//        return commentDao.getAllComments().map { allComments ->
//            Log.d("CommentsRepository", "All Room Comments: ${allComments.size}") // ✅ Log everything
//
//            val filteredComments = allComments.filter { comment ->
//                comment.commentId.contains(postId)
//            }
//            Log.d("CommentsRepository", "Filtered Comments: ${filteredComments.size}") // ✅ Check filtering
//
//            filteredComments
//        }
//    }
//
//}

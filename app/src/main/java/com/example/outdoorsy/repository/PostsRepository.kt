package com.example.outdoorsy.repository

import com.example.outdoorsy.model.dao.PostModel
import com.example.outdoorsy.model.dao.CommentModel
import com.example.outdoorsy.model.dao.FirebaseModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRepository @Inject constructor(
//    private val firestore: FirebaseFirestore
    private val firebaseModel: FirebaseModel

) {

    // Get posts for a specific user
    suspend fun getUserPosts(userId: String): List<PostModel> {
       return firebaseModel.getUserPosts(userId)
    }

    fun getPostLikesCount(postId: String, callback: (Long) -> Unit) {
        firebaseModel.getPostLikesCount(postId, callback)
    }


    // Fetch a single post by ID
    suspend fun getPostById(postId: String): PostModel {
        return firebaseModel.getPostById(postId)
    }

    // Get comments for a post
    suspend fun getCommentsForPost(postId: String): List<CommentModel> {
        return firebaseModel.getCommentsForPost(postId)
    }

    // Add a comment to a post
//    suspend fun addCommentToPost(postId: String, comment: CommentModel) {
//        firebaseModel.addCommentToPost(postId,comment)
//
//    }
    suspend fun addCommentToPost(postId: String, comment: CommentModel): Int {
        return firebaseModel.addCommentToPost(postId, comment)
    }

    // Toggle like on a post (increment or decrement like count)
    suspend fun toggleLike(postId: String, userId: String): Long {
        return firebaseModel.toggleLike(postId, userId)
    }


    suspend fun getFeedPosts(userId: String): List<PostModel> {
        return firebaseModel.getFeedPosts(userId)
    }
}



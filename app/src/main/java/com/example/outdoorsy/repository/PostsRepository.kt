
package com.example.outdoorsy.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.example.outdoorsy.model.CommentModel
import com.example.outdoorsy.model.dao.PostDao
import com.example.outdoorsy.model.PostModel
import com.example.outdoorsy.model.FirebaseModel
import com.example.outdoorsy.model.dao.AppLocalDb
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PostRepository @Inject constructor(
//    private val postDao: PostDao,  // 🔥 Inject Room Database DAO
    val firebaseModel: FirebaseModel, // Firebase
    val database: AppLocalDb// Firebase

) {
    val postDao=database.postDao()
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    // ✅ Get posts from Room (local cache)


    fun getLocalPosts(): LiveData<List<PostModel>> {
        return postDao.getAllPosts()
    }

suspend fun clearAllRoomPosts(){
    postDao.clearAllPosts()
}

    suspend fun getUserPosts(userId: String): List<PostModel> {
        return withContext(Dispatchers.IO) {
            // ✅ Clear all old posts from the local cache before fetching new ones
            postDao.clearAllPosts()

            val remotePosts = firebaseModel.getUserPosts(userId) // Fetch from Firebase
            Log.d("PostRepository", "Loaded ${remotePosts.size} posts from Firebase")

            postDao.insertPosts(remotePosts) // ✅ Store in Room
            remotePosts // ✅ Return fresh list
        }
    }



    // ✅ Get a post by ID
    suspend fun getPostById(postId: String): PostModel {
        return firebaseModel.getPostById(postId) // Fetching from Firebase
    }

    // ✅ Sync posts when user enters app
    suspend fun syncPostsFromFirebase(userId: String) {
        val remotePosts = firebaseModel.getUserPosts(userId)
        postDao.clearAllPosts()
        postDao.insertPosts(remotePosts)
    }



    // ✅ Delete post
    suspend fun deletePost(postId: String) {
        firebaseModel.deletePost(postId)  // Delete from Firebase
        postDao.deletePost(postId)  // Delete locally
    }

    // ✅ Update post
    suspend fun updatePost(postId: String, newText: String, newImageUrl: String?,oldImageUrl:String?) {
        firebaseModel.updatePost(
            postId, newText, newImageUrl!!,
            oldImageUrl = oldImageUrl
        )
        postDao.updatePost(postId, newText, newImageUrl)
    }

    // ✅ Comments
    suspend fun getCommentsForPost(postId: String): List<CommentModel> {
        return firebaseModel.getCommentsForPost(postId)
    }

    suspend fun addCommentToPost(postId: String, comment: CommentModel): Int {
        return firebaseModel.addCommentToPost(postId, comment)
    }

    // ✅ Likes
    fun fetchPostLikesCount(postId: String, callback: (Long) -> Unit) {
        firebaseModel.fetchPostLikesCount(postId, callback)
    }

    fun observePostLikesCount(postId: String, callback: (Long) -> Unit) {
        firebaseModel.observePostLikesCount(postId, callback)
    }

    fun getPostLikesCount(postId: String, callback: (Long) -> Unit) {
        firebaseModel.getPostLikesCount(postId, callback)
    }

    suspend fun toggleLike(postId: String, userId: String): Boolean {
        return firebaseModel.toggleLike(postId, userId)
    }

    suspend fun isLikedByUser(postId: String, userId: String): Boolean {
        return firebaseModel.checkIfLiked(postId, userId)
    }

    suspend fun getFeedPosts(userId: String): List<PostModel> {
        return firebaseModel.getFeedPosts(userId)
    }






}

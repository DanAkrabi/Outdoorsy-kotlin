
package com.example.outdoorsy.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.outdoorsy.model.CommentModel
import com.example.outdoorsy.model.dao.PostDao
import com.example.outdoorsy.model.PostModel
import com.example.outdoorsy.model.FirebaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val postDao: PostDao,  // 🔥 Inject Room Database DAO
    private val firebaseModel: FirebaseModel // Firebase
) {

    // ✅ Get posts from Room (local cache)
    fun getLocalPosts(): LiveData<List<PostModel>> = postDao.getAllPosts()

    // ✅ Get posts from Firebase (remote DB) and update Room
//    suspend fun getUserPosts(userId: String): List<PostModel> {
//        return withContext(Dispatchers.IO) {
//            val remotePosts = firebaseModel.getUserPosts(userId)
//            postDao.clearAllPosts()  // Refresh cache
//            postDao.insertPosts(remotePosts)  // Store in Room
//            remotePosts
//        }
//    }

    suspend fun getUserPosts(userId: String): List<PostModel> {
        return withContext(Dispatchers.IO) {
            val remotePosts = firebaseModel.getUserPosts(userId) // Fetch from Firebase
            Log.d("PostRepository", "Loaded ${remotePosts.size} posts from Room")
            postDao.clearAllPosts()
            postDao.insertPosts(remotePosts)
            Log.d("PostRepository", "Loaded ${remotePosts.size} posts from Firebase")
            remotePosts // ✅ Return plain list
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

    // ✅ Add post (Firebase + Room)
//    suspend fun insertPost(post: PostModel) {
//        firebaseModel.uploadPost(post)  // Upload to Firebase
//        postDao.insertPost(post) // Store in local cache
//    }

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

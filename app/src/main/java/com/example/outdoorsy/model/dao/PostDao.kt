package com.example.outdoorsy.model.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.example.outdoorsy.model.PostModel
import java.util.Date





@Dao
interface PostDao {


    // ✅ Insert a single post
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostModel)

    // ✅ Insert multiple posts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostModel>)

    // ✅ Fetch all posts (LiveData for UI updates)
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): LiveData<List<PostModel>>


    // ✅ Fetch all posts (suspend function for background tasks)
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    suspend fun getAllPostsSync(): List<PostModel>

    // ✅ Fetch user-specific posts (LiveData)
    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserPostsLive(userId: String): LiveData<List<PostModel>>

    // ✅ Fetch user-specific posts (suspend function for background processing)
    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getUserPostsSync(userId: String): List<PostModel>

    // ✅ Delete a single post by ID
    @Query("DELETE FROM posts WHERE postId = :postId")
    suspend fun deletePost(postId: String)

    // ✅ Clear all posts from Room
    @Query("DELETE FROM posts")
    suspend fun clearAllPosts()

    // ✅ Clear all posts of a specific user
    @Query("DELETE FROM posts WHERE userId = :userId")
    suspend fun clearUserPosts(userId: String)

    // ✅ Update a post’s text and image
    @Query("UPDATE posts SET textContent = :newText, imageUrl = :newImageUrl WHERE postId = :postId")
    suspend fun updatePost(postId: String, newText: String, newImageUrl: String?)

    // ✅ Count total posts in the local database
    @Query("SELECT COUNT(*) FROM posts")
    suspend fun getPostsCount(): Int
}







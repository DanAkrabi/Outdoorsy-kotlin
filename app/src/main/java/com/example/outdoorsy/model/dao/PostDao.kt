package com.example.outdoorsy.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.outdoorsy.model.PostModel
import java.util.Date

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostModel>)  // 🔥 Supports batch insert

    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): LiveData<List<PostModel>>

    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserPosts(userId: String): LiveData<List<PostModel>>

    @Query("DELETE FROM posts WHERE postId = :postId")
    suspend fun deletePost(postId: String)

    @Query("DELETE FROM posts")
    suspend fun clearAllPosts()

    @Query("UPDATE posts SET textContent = :newText, imageUrl = :newImageUrl WHERE postId = :postId")
    suspend fun updatePost(postId: String, newText: String, newImageUrl: String?)


}

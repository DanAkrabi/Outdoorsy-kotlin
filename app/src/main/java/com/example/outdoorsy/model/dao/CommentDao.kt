package com.example.outdoorsy.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.outdoorsy.model.CommentModel

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentModel)

    @Query("SELECT * FROM comments WHERE userId = :userId ORDER BY timestamp DESC")
    fun getCommentsByUser(userId: String): LiveData<List<CommentModel>>

    @Query("SELECT * FROM comments WHERE postId= :postId ORDER BY timestamp DESC")
    fun getCommentsForPost(postId: String): LiveData<List<CommentModel>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentModel>)

//    @Query("SELECT * FROM comments")
//    fun getAllComments(): LiveData<List<CommentModel>>

    @Query("DELETE FROM comments")
    suspend fun clearAllComments()

    @Query("SELECT * FROM comments ORDER BY timestamp DESC")
    fun getAllComments(): LiveData<List<CommentModel>>

}

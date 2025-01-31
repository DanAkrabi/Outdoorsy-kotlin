package com.example.outdoorsy.repository

import com.example.outdoorsy.model.dao.CommentModel
import com.example.outdoorsy.model.dao.FirebaseModel

import javax.inject.Inject

class CommentsRepository @Inject constructor(private val firebaseModel: FirebaseModel) {

    suspend fun getCommentsForPost(postId: String): List<CommentModel> {
        return firebaseModel.getCommentsForPost(postId)

    }


}

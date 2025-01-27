package com.example.outdoorsy.model.dao

import java.util.Date
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class PostModel(
    val postId: String,
    val userId: String,
    val textContent: String,
    val imageUrl: String?,
    val timestamp: Date,
    val location: String? = null,
    val likesCount: Int = 0,
    val commentsCount: Int = 0
):Parcelable

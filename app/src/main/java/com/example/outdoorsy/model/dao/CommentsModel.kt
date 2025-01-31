package com.example.outdoorsy.model.dao

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
@IgnoreExtraProperties
data class CommentModel(
    var commentId:String="",
    var userId: String = "",  // ID of the user who commented
    val content: String = "",  // The comment content
    val timestamp: Date = Date()  // Timestamp when the comment was posted
) : Parcelable

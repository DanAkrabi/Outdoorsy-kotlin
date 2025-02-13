package com.example.outdoorsy.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.outdoorsy.util.DateConverter
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "comments")
@TypeConverters(DateConverter::class)  // Convert Date <-> Long
data class CommentModel(
    @PrimaryKey var commentId: String = "",
    var userId: String = "",
    val content: String = "",
    val timestamp: Date = Date(),
    val postId: String = ""
) : Parcelable


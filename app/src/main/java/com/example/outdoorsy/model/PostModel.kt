package com.example.outdoorsy.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date
import android.os.Parcelable
import com.example.outdoorsy.util.DateConverter
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
@Entity(tableName = "posts")
@TypeConverters(DateConverter::class)  // Converts Date <-> Long
data class PostModel(
    @PrimaryKey val postId: String = "",
    val userId: String = "",
    val textContent: String = "",
    val imageUrl: String? = null,
    val timestamp: Date = Date(),
    val location: String? = null,
    val likesCount: Long = 0,
    val commentsCount: Int = 0,
    val fullname: String = "",
    val profileImg: String? = null
) : Parcelable


//package com.example.outdoorsy.model.dao
//
//import java.util.Date
//import android.os.Parcelable
//import com.google.firebase.firestore.IgnoreExtraProperties
//import kotlinx.parcelize.Parcelize
//@IgnoreExtraProperties
//@Parcelize
//
//data class PostModel(
//    val postId: String = "",
//    val userId: String = "",
//    val textContent: String = "",
//    val imageUrl: String? = null,
//    val timestamp: Date = Date(),
//    val location: String? = null,
//    val likesCount: Long = 0,
//    val commentsCount: Int = 0,
//    val fullname:String="",
//    var ProfileImg:String?=null
//) : Parcelable

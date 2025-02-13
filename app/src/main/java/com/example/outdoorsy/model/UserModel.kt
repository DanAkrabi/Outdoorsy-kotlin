package com.example.outdoorsy.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserModel(
    @PrimaryKey var id: String = "",
    val fullname: String = "",
    val email: String = "",
    val password: String = "",
    val profileImg: String? = null,
    val bio: String? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0
)


//package com.example.outdoorsy.model.dao
//
//data class UserModel(
//    var id: String = "",
//    val fullname: String = "",
//    val email: String = "",
//    val password:String="",
//    val profileImg: String? = null,
//    val bio:String?=null,
//    val followersCount:Int=0,
//    val followingCount:Int=0,
//
//)

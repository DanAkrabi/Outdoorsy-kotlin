package com.example.outdoorsy.model.dao

data class UserModel(
    var id: String = "",
    val fullname: String = "",
    val email: String = "",
    val password:String="",
    val profileImg: String? = null,
    val bio:String?=null,
    val followersCount:Int=0,
    val followingCount:Int=0,
)

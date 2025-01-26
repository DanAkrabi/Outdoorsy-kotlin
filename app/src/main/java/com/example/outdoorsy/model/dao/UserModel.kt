package com.example.outdoorsy.model.dao

data class UserModel(
    val id: String = "",
    val fullname: String = "",
    val email: String = "",
    val password:String="",
    val profileImg: String? = null,
    val bio:String?=null
)

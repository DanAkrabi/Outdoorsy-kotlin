package com.example.outdoorsy.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.outdoorsy.model.UserModel

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserModel)

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): LiveData<UserModel>
}

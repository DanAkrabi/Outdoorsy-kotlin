package com.example.outdoorsy.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.outdoorsy.model.UserModel

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserModel)

    @Update
    suspend fun updateUser(user: UserModel): Int

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): LiveData<UserModel>

    @Query("DELETE FROM users")
    suspend fun clearUsers()

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserByIdSync(userId: String): UserModel?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserLiveData(userId: String): LiveData<UserModel>
}


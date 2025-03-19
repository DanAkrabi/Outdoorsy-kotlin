package com.example.outdoorsy.model.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.outdoorsy.model.CommentModel
import com.example.outdoorsy.model.PostModel
import com.example.outdoorsy.model.UserModel

@Database(entities = [UserModel::class, PostModel::class, CommentModel::class], version = 5, exportSchema = false)
abstract class AppLocalDb : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao

    companion object {
        @Volatile
        private var INSTANCE: AppLocalDb? = null

        fun getDatabase(context: Context): AppLocalDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppLocalDb::class.java,
                    "outdoorsy_database"
                ).fallbackToDestructiveMigration() // ✅ Auto-migrates if version changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


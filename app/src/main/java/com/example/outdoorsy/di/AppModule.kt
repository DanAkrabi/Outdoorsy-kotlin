package com.example.outdoorsy.di

import android.content.Context
import com.example.outdoorsy.model.dao.AppLocalDb
import com.example.outdoorsy.model.dao.CommentDao
import com.example.outdoorsy.model.dao.PostDao
import com.example.outdoorsy.model.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppLocalDb {
        return AppLocalDb.getDatabase(context)
    }

    @Provides
    @Singleton
    fun providePostDao(database: AppLocalDb): PostDao {
        return database.postDao()
    }

    @Provides
    @Singleton
    fun provideCommentDao(database: AppLocalDb): CommentDao {
        return database.commentDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppLocalDb): UserDao {
        return database.userDao()
    }
}

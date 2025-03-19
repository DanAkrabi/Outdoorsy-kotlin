package com.example.outdoorsy.di

import android.content.Context
import com.example.outdoorsy.data.api.RetrofitInstance
import com.example.outdoorsy.data.api.WeatherApiService
import com.example.outdoorsy.model.dao.AppLocalDb
import com.example.outdoorsy.model.dao.CommentDao
import com.example.outdoorsy.model.dao.PostDao
import com.example.outdoorsy.model.dao.UserDao
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
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

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface RemoteMediatorEntryPoint {
        fun getFirebaseFirestore(): FirebaseFirestore
        fun getAppLocalDb(): AppLocalDb
    }
    // Provide the WeatherApiService
    @Provides
    @Singleton
    fun provideWeatherApiService(): WeatherApiService {
        return RetrofitInstance.api // This is the instance of WeatherApiService created in RetrofitInstance
    }

}

package com.example.outdoorsy.di

import com.example.outdoorsy.model.dao.FirebaseModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    fun provideFirebaseModel(firestore: FirebaseFirestore): FirebaseModel {
        return FirebaseModel(firestore) // ✅ No UserViewModel here
    }
}



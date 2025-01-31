package com.example.outdoorsy.di

import com.example.outdoorsy.model.dao.FirebaseModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val firestore = Firebase.firestore
//    init {
//        val setting = firestoreSettings {
//            setLocalCacheSettings(memoryCacheSettings {
//
//            })
//        }
//        database.firestoreSettings = setting
//    }
        // Configure Firestore settings here, before any other Firestore operations
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true) // Enable offline persistence
            .build()
        firestore.firestoreSettings = settings

        return firestore
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    @Singleton
    fun provideFirebaseModel(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): FirebaseModel {
        return FirebaseModel( firestore,firebaseAuth)
    }
}


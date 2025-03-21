
package com.example.outdoorsy.di

import android.content.Context
import com.example.outdoorsy.model.CloudinaryModel
import com.example.outdoorsy.model.FirebaseModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val firestore = Firebase.firestore
        // You can still customize other settings if needed, but persistence is managed automatically.
        val settings = FirebaseFirestoreSettings.Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)  // Optional: Customize cache size
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
    fun provideCloudinaryModel(@ApplicationContext context: Context): CloudinaryModel {
        return CloudinaryModel(context)
    }

    @Provides
    @Singleton
    fun provideFirebaseModel(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        cloudinaryModel: CloudinaryModel
    ): FirebaseModel {
        return FirebaseModel(firestore, firebaseAuth, cloudinaryModel)
    }
}



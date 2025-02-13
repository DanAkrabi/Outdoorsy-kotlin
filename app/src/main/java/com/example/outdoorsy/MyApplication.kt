package com.example.outdoorsy

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null

        // Public static method to access the context globally
        val globalContext: Context
            get() = instance!!.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this) // Initialize Firebase when the application starts
    }
}


//package com.example.outdoorsy
//
//import android.app.Application
//import com.google.firebase.FirebaseApp
//import dagger.hilt.android.HiltAndroidApp
//
//@HiltAndroidApp
//class MyApplication : Application() {
//    override fun onCreate() {
//        super.onCreate()
//        FirebaseApp.initializeApp(this)
//    }
//}

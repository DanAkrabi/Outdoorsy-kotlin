package com.example.outdoorsy.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.outdoorsy.model.dao.FirebaseModel
import com.google.firebase.firestore.FirebaseFirestore

class PostViewModel : ViewModel() {
    // Initialize Firestore
    private val firestoreInstance = FirebaseFirestore.getInstance()

    // Create FirebaseModel with Firestore instance
    private val firebaseModel = FirebaseModel(firestoreInstance)

    fun uploadImageToFirebase(image: Bitmap, name: String, callback: (String?) -> Unit) {
        firebaseModel.uploadImage(image, name, callback)
    }
}

package com.example.outdoorsy.repository

import android.graphics.Bitmap
import com.example.outdoorsy.model.FirebaseModel
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CameraRepository @Inject constructor(
    private val firebaseModel: FirebaseModel
) {
    suspend fun uploadImageToCloudinary(bitmap: Bitmap,imageName: String,onSuccess: (String?) -> Unit,onError: (String?) -> Unit): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            firebaseModel.uploadImageToCloudinary(
                bitmap,
                imageName,
                onSuccess = onSuccess,
                onError = onError
            )


        }
    }





    fun uploadPost(bitmap:Bitmap,imageName: String,description: String, onSuccess: (String?) -> Unit, onError: (String?) -> Unit){
            firebaseModel.createPost(
                bitmap, imageName,
                textContent = description,
                onSuccess = onSuccess,
                onError = onError,
            )
    }

    fun updatePostDetails(
        postId: String,
        url: String?,
        description: String,
        onSuccess: (String?) -> Unit,
        onError: (String?) -> Unit
    ) {
        firebaseModel.updatePostDetails(
            postId = postId,
            newImageUrl = url,
            newTextContent = description,
            onSuccess = onSuccess,  // ✅ Pass success callback
            onError = onError       // ✅ Pass error callback
        )
    }

    fun deleteImageFromCloudinary(imageUrl: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
       firebaseModel.deleteImageFromCloudinary(imageUrl, onSuccess, onError)
    }



}


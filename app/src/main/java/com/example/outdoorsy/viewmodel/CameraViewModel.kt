package com.example.outdoorsy.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.repository.CameraRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraRepository: CameraRepository,
    private val application: Application // Inject the Application instance
) : AndroidViewModel(application) { // Extend AndroidViewModel

    private val _uploadResult = MutableLiveData<Result<String>>()
    val uploadResult: LiveData<Result<String>> = _uploadResult

    private val _cameraPermissionGranted = MutableLiveData<Boolean>()
    val cameraPermissionGranted: LiveData<Boolean> = _cameraPermissionGranted

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    private val _capturePhotoError = MutableLiveData<String?>()
    val capturePhotoError: LiveData<String?> = _capturePhotoError

    private val _launchCamera = MutableLiveData<Uri?>()
    val launchCamera: LiveData<Uri?> = _launchCamera

    private val _displayImage = MutableLiveData<Uri?>()
    val displayImage: LiveData<Uri?> = _displayImage

    private val _isUploading = MutableLiveData<Boolean>(false)
    val isUploading: LiveData<Boolean> = _isUploading

    val _navigateToProfile = MutableLiveData<String?>()
    val navigateToProfile: LiveData<String?> = _navigateToProfile


    fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                application,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            _cameraPermissionGranted.value = true
            capturePhoto()
        } else {
            _cameraPermissionGranted.value = false
        }
    }

    fun setImageUri(uri:Uri){
        _imageUri.value = uri
        _imageUri.postValue(uri)

    }

    fun onCameraPermissionResult(isGranted: Boolean) {
        _cameraPermissionGranted.value = isGranted
        if (isGranted) {
            capturePhoto()
        }
    }

    fun capturePhoto() {
        try {
            Log.d("CameraViewModel", "Attempting to capture photo")
            val photoFile: File = createImageFile()
            val authority = "${application.packageName}.provider"
            val uri = FileProvider.getUriForFile(application, authority, photoFile)
            _imageUri.value = uri
            _launchCamera.value = uri
            Log.d("CameraViewModel", "New image URI set: $uri")
        } catch (e: Exception) {
            Log.e("CameraViewModel", "Error capturing photo", e)
            _capturePhotoError.value = "Error capturing photo: ${e.message}"
        }
    }

    fun onTakePictureResult(success: Boolean) {
        if (success) {
            Log.d("CameraViewModel", "Photo capture successful")
            _imageUri.value?.let {
                _displayImage.value = it
            }
        } else {
            Log.e("CameraViewModel", "Failed to capture photo")
            _capturePhotoError.value = "Failed to capture photo"
        }
    }

    private fun createImageFile(): File {
        val storageDir: File? = application.getExternalFilesDir("Pictures") // Match file_paths.xml
        return File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            Log.d("CameraViewModel", "Image file created: ${absolutePath}")
        }
    }



//    private fun uriToBitmap(uri: Uri): Bitmap? {
//        return try {
//            application.contentResolver.openInputStream(uri)?.use {
//                BitmapFactory.decodeStream(it)
//            }
//        } catch (e: Exception) {
//            Log.e("CameraViewModel", "Error decoding bitmap from URI: $e")
//            null
//        }
//    }
private fun uriToBitmap(uri: Uri): Bitmap? {
    return try {
        val inputStream = application.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        // 🔥 Rotate the bitmap before returning it
        bitmap?.let { rotateBitmapIfNeeded(uri, it) }
    } catch (e: Exception) {
        Log.e("CameraViewModel", "Error decoding bitmap from URI: $e")
        null
    }
}
    private fun rotateBitmapIfNeeded(imageUri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            val inputStream = application.contentResolver.openInputStream(imageUri)
            val exif = inputStream?.let { ExifInterface(it) }
            inputStream?.close()

            val orientation = exif?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            ) ?: ExifInterface.ORIENTATION_NORMAL

            val matrix = Matrix()

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }

            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            Log.e("CameraViewModel", "Error rotating bitmap: $e")
            bitmap // If an error occurs, return the original bitmap
        }
    }


//    fun uploadImage(imageUri: Uri, description: String) {
//        try {
//            Log.d("CameraViewModel", "Loading bitmap from URI: $imageUri")
//
//            val bitmap = uriToBitmap(imageUri) ?: run {
//                Log.e("CameraViewModel", "Failed to decode bitmap from URI")
//                _capturePhotoError.value = "Failed to decode image"
//                return
//            }
//
//            val imageName = "image_${System.currentTimeMillis()}_${description.filter { it.isLetterOrDigit() }}"
//
//            if (_isUploading.value == true) {
//                Log.d("CameraViewModel", "Upload already in progress.")
//                return
//            }
//
//            _isUploading.value = true
//
//            viewModelScope.launch {
//                cameraRepository.uploadImageToCloudinary(
//                    bitmap = bitmap,
//                    imageName = imageName,
//                    onSuccess = { url ->
//                        Log.d("CameraViewModel", "Image uploaded successfully: $url")
//
//                        createPost(bitmap, url!!, description){ postId ->
//                            Log.d("CameraViewModel", "Post created successfully: $postId")
//
//                            // ✅ Only navigate AFTER post is successfully uploaded
//                            _navigateToProfile.postValue(FirebaseAuth.getInstance().currentUser?.uid)
//
//                        }
//
//
//                        // ✅ Notify CameraFragment to navigate
//                    },
//                    onError = { error ->
//                        Log.e("CameraViewModel", "Error uploading image: $error")
//                        Toast.makeText(application, "Error uploading image: $error", Toast.LENGTH_LONG).show()
//                        _uploadResult.postValue(Result.failure(Exception(error)))
//                        _isUploading.postValue(false)
//                    }
//                )
//            }
//        } catch (e: Exception) {
//            Log.e("CameraViewModel", "Error loading image", e)
//            _capturePhotoError.value = "Error loading image: ${e.message}"
//        }
//    }
fun uploadImage(imageUri: Uri, description: String) {
    try {
        Log.d("CameraViewModel", "Loading and rotating bitmap from URI: $imageUri")

        val bitmap = uriToBitmap(imageUri) ?: run {
            Log.e("CameraViewModel", "Failed to decode bitmap from URI")
            _capturePhotoError.value = "Failed to decode image"
            return
        }

        val imageName = "image_${System.currentTimeMillis()}_${description.filter { it.isLetterOrDigit() }}"

        if (_isUploading.value == true) {
            Log.d("CameraViewModel", "Upload already in progress.")
            return
        }

        _isUploading.value = true

        viewModelScope.launch {
            cameraRepository.uploadImageToCloudinary(
                bitmap = bitmap,  // ✅ Upload rotated bitmap
                imageName = imageName,
                onSuccess = { url ->
                    Log.d("CameraViewModel", "Image uploaded successfully: $url")

                    createPost(bitmap, url!!, description) { postId ->
                        Log.d("CameraViewModel", "Post created successfully: $postId")
                        _navigateToProfile.postValue(FirebaseAuth.getInstance().currentUser?.uid)
                    }
                },
                onError = { error ->
                    Log.e("CameraViewModel", "Error uploading image: $error")
                    Toast.makeText(application, "Error uploading image: $error", Toast.LENGTH_LONG).show()
                    _uploadResult.postValue(Result.failure(Exception(error)))
                    _isUploading.postValue(false)
                }
            )
        }
    } catch (e: Exception) {
        Log.e("CameraViewModel", "Error loading image", e)
        _capturePhotoError.value = "Error loading image: ${e.message}"
    }
}


    private fun createPost(bitmap: Bitmap, imageUrl: String, description: String, onSuccess: (String) -> Unit) {
        cameraRepository.uploadPost(
            bitmap, imageUrl, description,
            onSuccess = { postId ->
                Log.d("CameraViewModel", "Post created successfully: $postId")
                Toast.makeText(application, "Post created successfully!", Toast.LENGTH_LONG).show()
                _isUploading.postValue(false)

                // ✅ Call the success callback to navigate
                onSuccess(postId!!)
            },
            onError = { error ->
                Log.e("CameraViewModel", "Error creating post: $error")
                Toast.makeText(application, "Error creating post: $error", Toast.LENGTH_LONG).show()
                _isUploading.postValue(false)
            }
        )
    }

    /**
     * ✅ Converts content:// URI to Bitmap safely.
     */



}

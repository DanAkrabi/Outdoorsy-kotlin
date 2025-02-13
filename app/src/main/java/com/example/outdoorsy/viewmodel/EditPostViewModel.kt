package com.example.outdoorsy.viewmodel


import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.outdoorsy.repository.PostRepository

import com.example.outdoorsy.databinding.FragmentEditPostBinding
import com.example.outdoorsy.repository.CameraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val cameraRepository: CameraRepository,
    private val application: Application
) : ViewModel() {

    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!
//    private val viewModel: EditPostViewModel by viewModels()

    val postText = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String?>()
    val postId = MutableLiveData<String>()
    private lateinit var oldImageUrl: String // Store the old image URL

    fun loadPostDetails(postId: String) {
        viewModelScope.launch {
            val post = postRepository.getPostById(postId)
            post?.let {
                postText.postValue(it.textContent)
                imageUrl.postValue(it.imageUrl)
            }
        }
    }


    fun updatePost(postId: String, description: String, newImageUrl: String, oldImageUrl: String?) {
        Log.d("EditPostViewModel", "newImageUrl: $newImageUrl")
        Log.d("EditPostViewModel", "oldImageUrl: $oldImageUrl")

        if (!oldImageUrl.isNullOrEmpty() && oldImageUrl != newImageUrl) {
            val bitmap = uriToBitmap(newImageUrl.toUri())
            if (bitmap == null) {
                Log.e("EditPostViewModel", "Failed to decode bitmap from URI")
                return
            }

            val imageName = "image_${System.currentTimeMillis()}_${description.filter { it.isLetterOrDigit() }}"

            viewModelScope.launch {
                cameraRepository.uploadImageToCloudinary(
                    bitmap = bitmap,
                    imageName = imageName,
                    onSuccess = { url ->
                        Log.d("EditPostViewModel", "Image uploaded successfully: $url")

                        // ✅ Update Firestore with new image and description
                        cameraRepository.updatePostDetails(
                            postId = postId,
                            url = url,
                            description = description,
                            onSuccess = { message ->
                                Log.d("EditPostViewModel", "Post updated successfully: $message")

                                // ✅ Now delete the old image from Cloudinary
                                cameraRepository.deleteImageFromCloudinary(
                                    oldImageUrl,
                                    onSuccess = {
                                        Log.d("EditPostViewModel", "Image deleted successfully from Cloudinary: $oldImageUrl")
                                    },
                                    onError = { error ->
                                        Log.e("EditPostViewModel", "Error deleting image from Cloudinary: $error")
                                    }
                                )
                            },
                            onError = { error ->
                                Log.e("EditPostViewModel", "Error updating post: $error")
                            }
                        )
                    },
                    onError = { error ->
                        Log.e("EditPostViewModel", "Error uploading image: $error")
                    }
                )
            }
        }
    }




    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            application.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: Exception) {
            Log.e("EditPostViewModel", "Error decoding bitmap from URI: $e")
            null
        }
    }


    fun deletePost() {
        val id = postId.value ?: return

        viewModelScope.launch {
            postRepository.deletePost(id).let { result ->
                // handle result, e.g., navigate back or show an error
            }
        }
    }



}


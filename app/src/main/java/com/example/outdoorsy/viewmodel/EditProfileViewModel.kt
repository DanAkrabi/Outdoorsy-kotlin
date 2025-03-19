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
import com.example.outdoorsy.model.FirebaseModel
import com.example.outdoorsy.model.dao.AppLocalDb
import com.example.outdoorsy.repository.CameraRepository
import com.example.outdoorsy.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val application: Application,
    private val cameraRepository: CameraRepository,
    private val database:AppLocalDb
) : ViewModel() {
    val currentUser = FirebaseAuth.getInstance().currentUser
    private val storageRef = FirebaseStorage.getInstance().reference
    val profileFullName = MutableLiveData<String>()
    val currentImageUrl = MutableLiveData<String?>() // Existing image URL loaded from Firebase
    val newImageUri = MutableLiveData<Uri?>()
    val userDao=database.userDao()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        currentUser?.let {
            profileFullName.value = it.displayName
            currentImageUrl.value = it.photoUrl.toString()
        }
    }

fun updateUserProfile(
    fullName: String,
    newImageUri: Uri?,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val currentImageUrlValue = currentImageUrl.value

    // If there's a new image
    if (newImageUri != null) {
        val bitmap = uriToBitmap(newImageUri)
        if (bitmap == null) {
            onError("Failed to decode bitmap from URI")
            return
        }

        val imageName = "image_${System.currentTimeMillis()}"
        viewModelScope.launch {
            cameraRepository.uploadImageToCloudinary(
                bitmap = bitmap,
                imageName = imageName,
                onSuccess = { uploadedImageUrl ->
                    Log.d("EditProfile", "Image uploaded successfully: $uploadedImageUrl")

                    // After uploading image, update user profile in Firestore, FirebaseAuth, and Room
                    userRepository.updateUserProfile(
                        fullName = fullName,
                        newImageUrl = uploadedImageUrl,
                        currentImageUrl = currentImageUrlValue,
                        onSuccess = {
                            handleProfileUpdateSuccess(fullName, uploadedImageUrl, onSuccess, onError)
                        },
                        onError = onError
                    )
                },
                onError = { error ->
                    Log.e("EditProfile", "Error uploading image: $error")
                    onError(error!!)
                }
            )
        }
    } else {
        // No new image, just update the full name
        userRepository.updateUserProfile(
            fullName = fullName,
            newImageUrl = null,
            currentImageUrl = currentImageUrlValue,
            onSuccess = {
                handleProfileUpdateSuccess(fullName, currentImageUrlValue, onSuccess, onError)
            },
            onError = onError
        )
    }
}



    private fun handleProfileUpdateSuccess(
        fullName: String,
        newImageUrl: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Reload FirebaseAuth user to ensure the profile data is up to date
        currentUser?.reload()?.addOnCompleteListener { reloadTask ->
            if (reloadTask.isSuccessful) {
                val updatedUser = FirebaseAuth.getInstance().currentUser
                profileFullName.postValue(updatedUser?.displayName)
                currentImageUrl.postValue(updatedUser?.photoUrl?.toString())

                // Now update Room with the new name and profile image URL
                updatedUser?.uid?.let { uid ->
                    viewModelScope.launch {
                        val localUser = userDao.getUserByIdSync(uid)
                        if (localUser != null) {
                            // Create an updated UserModel
                            val updatedUserModel = localUser.copy(
                                fullname = fullName,  // Update name
                                profileImg = newImageUrl // Update profile image URL
                            )
                            // Update the user in Room
                            val updateResult = userDao.updateUser(updatedUserModel)

                            // If the update is successful, navigate to the success callback
                            if (updateResult > 0) {
                                onSuccess()
                            } else {
                                onError("Failed to update Room database")
                            }
                        } else {
                            onError("User not found in Room database")
                        }
                    }
                } ?: onError("Failed to retrieve Firebase user UID")

            } else {
                onError("Failed to refresh Firebase user: ${reloadTask.exception?.message}")
            }
        }
    }


    fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            application.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: Exception) {
            Log.e("EditProfile", "EditProfileViewModel - Error decoding bitmap from URI: $e")
            null
        }
    }

//fun updateUserProfile(fullName: String, newImageUri: String?, onSuccess: () -> Unit, onError: (String) -> Unit) {
//    if (newImageUri != null) {
//        userRepository.updateUserProfile(
//            fullName = fullName,
//            newImageUrl = newImageUri,
//            currentImageUrl = currentImageUrl.value,
//            onSuccess = {
//                currentUser?.reload()?.addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        onSuccess()
//                    } else {
//                        onError(task.exception?.message ?: "Failed to refresh user")
//                    }
//                }
//            },
//            onError = onError
//        )
//    } else {
//        userRepository.updateUserProfile(
//            fullName = fullName,
//            newImageUrl = null,
//            currentImageUrl = currentImageUrl.value,
//            onSuccess = {
//                currentUser?.reload()?.addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        onSuccess()
//                    } else {
//                        onError(task.exception?.message ?: "Failed to refresh user")
//                    }
//                }
//            },
//            onError = onError
//        )
//    }
//}




}



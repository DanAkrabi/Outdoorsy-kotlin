package com.example.outdoorsy.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class CloudinaryModel @Inject constructor(@ApplicationContext private val context: Context) {

    init {
        val config = mapOf(
            "cloud_name" to "doafwoglp",
            "api_key" to "744644397642942",
            "api_secret" to "080V466KGubZchpz6LWEk_B8pH8"
        )
        MediaManager.init(context, config)
    }

    // ✅ Upload a single image
    fun uploadImage(
        bitmap: Bitmap,
        name: String,
        onSuccess: (String?) -> Unit,
        onError: (String) -> Unit
    ) {
        val file = bitmapToFile(bitmap, name)
        MediaManager.get().upload(file.path)
            .option("folder", "images")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    Log.d("Cloudinary", "Upload started: $requestId")
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    val progress = (bytes.toDouble() / totalBytes) * 100
                    Log.d("Cloudinary", "Upload progress: $progress%")
                }

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val url = resultData?.get("secure_url") as? String ?: ""
                    Log.d("Cloudinary", "Image uploaded successfully: $url")
                    onSuccess(url)
                    file.delete()  // Clean up the file after uploading
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Log.e("Cloudinary", "Upload error: ${error?.description}")
                    onError(error?.description ?: "Unknown error")
                    file.delete()  // Clean up the file after a failed upload
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    Log.w("Cloudinary", "Upload reschedule: ${error?.description}")
                }
            }).dispatch()
    }


fun deleteImageFromCloudinary(imageUrl: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    try {
        Log.d("Cloudinary", "Attempting to delete image: $imageUrl")

        val publicId = extractPublicId(imageUrl)

        Log.d("Cloudinary", "Extracted public ID for deletion: $publicId")

        // ✅ Run in background thread to avoid blocking the main thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = MediaManager.get().cloudinary.uploader().destroy(publicId, mapOf("invalidate" to true))

                Log.d("Cloudinary", "Cloudinary delete response: $result")

                if (result["result"] == "ok") {
                    Log.d("Cloudinary", "Image deleted successfully: $publicId")
                    withContext(Dispatchers.Main) { onSuccess() }
                } else {
                    val errorMsg = "Cloudinary deletion failed: ${result["result"]}"
                    Log.e("Cloudinary", errorMsg)
                    withContext(Dispatchers.Main) { onError(errorMsg) }
                }
            } catch (e: Exception) {
                Log.e("Cloudinary", "Exception while deleting image: ${e.message}")
                withContext(Dispatchers.Main) { onError(e.message ?: "Unknown error") }
            }
        }
    } catch (e: Exception) {
        Log.e("Cloudinary", "Error preparing image deletion: ${e.message}")
        onError(e.message ?: "Unknown error")
    }
}


    private fun extractPublicId(imageUrl: String): String {
        Log.d("Cloudinary", "Original URL received: $imageUrl")

        val publicId = imageUrl
            .substringAfter("upload/")  // Get everything after "upload/"
            .substringAfter("/")        // Remove version (e.g., "v1739360516/")
            .substringBeforeLast(".")   // Remove file extension (e.g., ".jpg")

        Log.d("Cloudinary", "Extracted public ID: $publicId")

        return publicId
    }

    private fun bitmapToFile(bitmap: Bitmap, name: String): File {
        val file = File(context.cacheDir, "image_$name.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return file
    }
}


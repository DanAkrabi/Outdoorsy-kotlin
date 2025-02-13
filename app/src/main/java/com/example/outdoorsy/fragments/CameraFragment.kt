package com.example.outdoorsy.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.outdoorsy.R
import com.example.outdoorsy.viewmodel.CameraViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private lateinit var imageUri: Uri
    private lateinit var imageView: ImageView
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private val cameraViewModel: CameraViewModel by viewModels()
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var uploadImageButton: Button//upload post button
    private lateinit var descriptionEditText: TextInputEditText
    private lateinit var openGalleryLauncher: ActivityResultLauncher<String>
    private lateinit var buttonOpenGallery: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                cameraViewModel.setImageUri(uri) // Update ViewModel
                imageView.setImageURI(uri) // Display selected image
            } else {
                Log.e("CameraFragment", "Gallery selection failed")
                Toast.makeText(requireContext(), "Failed to select image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        imageView = view.findViewById(R.id.imageViewCaptured)
        val takePictureButton = view.findViewById<Button>(R.id.take_picture_button)
        uploadImageButton = view.findViewById(R.id.upload_button)
        descriptionEditText = view.findViewById(R.id.editTextDescription)


        // Register activity result launcher for capturing images
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                cameraViewModel.onTakePictureResult(success)
        }

        buttonOpenGallery = view.findViewById(R.id.button_open_gallery)

        buttonOpenGallery.setOnClickListener {
            Log.d("CameraFragment", "Opening gallery...")
            openGalleryLauncher.launch("image/*") // Open gallery
        }

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            cameraViewModel.onCameraPermissionResult(isGranted)
        }

        buttonOpenGallery.setOnClickListener {
            Log.d("CameraFragment", "Opening gallery...")
            openGalleryLauncher.launch("image/*") // Open gallery
        }
        // Click listener for the capture button
        takePictureButton.setOnClickListener {
            Log.d("CameraFragment", "Capture photo button clicked")
            cameraViewModel.checkCameraPermission()
        }
        uploadImageButton.setOnClickListener {
            Log.d("CameraFragment", "Upload image button clicked")

            val description = descriptionEditText.text.toString()
            uploadImageButton.isEnabled = false  // Disable the button to prevent further clicks
            cameraViewModel.imageUri.value?.let { uri ->
                cameraViewModel.uploadImage(uri, description)
                findNavController().navigate(R.id.action_cameraFragment_to_profileFragment)
            }
        }


        observerCamera()


        return view
    }





    companion object {
        const val REQUEST_CAMERA_PERMISSION = 1
    }

    private fun observerCamera(){

        cameraViewModel.navigateToProfile.observe(viewLifecycleOwner) { userId ->
            if (userId != null) {
                val action = CameraFragmentDirections.actionCameraFragmentToProfileFragment(userId)
                findNavController().navigate(action)

                // ✅ Reset LiveData after navigating to prevent multiple triggers
                cameraViewModel._navigateToProfile.value = null
            }
        }

        cameraViewModel.uploadResult.observe(viewLifecycleOwner, Observer { result ->
            if (result.isSuccess) {
                val url = result.getOrNull()  // This will get you the URL string
                Log.d("CameraFragment:", "Upload successful: $url")
                Toast.makeText(requireContext(), "Upload successful", Toast.LENGTH_SHORT).show()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                Log.d("CameraFragment:", "Upload failed: $error")
                Toast.makeText(requireContext(), "Upload failed: $error", Toast.LENGTH_SHORT).show()
            }
            uploadImageButton.isEnabled = true  // Re-enable the button after the upload is done or fails
        })
        cameraViewModel.cameraPermissionGranted.observe(viewLifecycleOwner, Observer { isGranted ->
            if (!isGranted) {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        })
        cameraViewModel.launchCamera.observe(viewLifecycleOwner, Observer { uri ->
            if (uri != null) {
                takePictureLauncher.launch(uri)
            }
        })
        cameraViewModel.capturePhotoError.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
        })
        cameraViewModel.displayImage.observe(viewLifecycleOwner, Observer { uri ->
            if (uri != null) {
                imageView.setImageURI(uri)
            }
        })
    }


}

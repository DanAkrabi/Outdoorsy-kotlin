package com.example.outdoorsy.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.outdoorsy.R

class CameraFragment : Fragment(R.layout.fragment_camera) {
    private lateinit var cameraLauncher: ActivityResultLauncher<Void?>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                val imageView = view.findViewById<ImageView>(R.id.imageViewCaptured)
                imageView.setImageBitmap(it)
            } ?: Toast.makeText(requireContext(), "Failed to capture photo", Toast.LENGTH_SHORT).show()
        }

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraLauncher.launch(null)
            } else {
                Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }

        val takePictureButton = view.findViewById<Button>(R.id.take_picture_button)
        takePictureButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraLauncher.launch(null)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        return view
    }
}

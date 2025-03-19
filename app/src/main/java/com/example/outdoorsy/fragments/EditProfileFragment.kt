package com.example.outdoorsy.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.outdoorsy.databinding.FragmentEditProfileBinding
import com.example.outdoorsy.viewmodel.EditProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditProfileViewModel by viewModels()
    private var oldProfileUrl: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fullname = arguments?.getString("fullname") ?: "Default Name"
        val imageUrl = arguments?.getString("imageUrl") ?: "Default Image URL"
        // Use the data to populate your views or for other logic
        binding.fullNameEditText.setText(fullname)
        Glide.with(this).load(imageUrl).into(binding.profileImageView)

        binding.buttonChooseImage.setOnClickListener {
            openGallery()
        }


        binding.buttonSave.setOnClickListener {
            val fullName = binding.fullNameEditText.text.toString()
            val newImageUri = viewModel.currentImageUrl.value?.toUri() ?: Uri.EMPTY

            lifecycleScope.launch {
                viewModel.updateUserProfile(fullName, newImageUri, {
                    // ✅ Only navigate if Fragment is still attached
                    if (isAdded) {
                        findNavController().popBackStack()
                    }
                }, { error ->
                    Log.e("EditProfile", "Error updating profile: $error")
                })
            }
        }

        viewModel.profileFullName.observe(viewLifecycleOwner) { fullName ->
            binding.fullNameEditText.setText(fullName)
        }

        viewModel.currentImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            Glide.with(this).load(imageUrl).into(binding.profileImageView)
        }



        viewModel.loadUserProfile()

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                viewModel.currentImageUrl.value = uri.toString()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1000
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

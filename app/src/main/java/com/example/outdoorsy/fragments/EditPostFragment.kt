package com.example.outdoorsy.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.outdoorsy.R
import com.example.outdoorsy.databinding.FragmentEditPostBinding
import com.example.outdoorsy.viewmodel.EditPostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPostFragment : Fragment() {
    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditPostViewModel by viewModels()
    private lateinit var postId: String
    private var oldImageUrl: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = EditPostFragmentArgs.fromBundle(requireArguments()) // Get SafeArgs
        postId = args.postId
        oldImageUrl = args.imageUrl
        // Set the initial values in the ViewModel
        viewModel.imageUrl.value = args.imageUrl
        viewModel.postText.value = args.textContent


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.buttonChooseImage.setOnClickListener {
            openGallery()
        }

        binding.buttonSave.setOnClickListener(){
            saveUpdatedPost()
        }

        setupObservers()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                viewModel.imageUrl.value = uri.toString()
                Glide.with(this).load(uri).into(binding.imageViewPost)
            }
        }
    }


    private fun setupObservers() {
        viewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            Glide.with(this).load(url).into(binding.imageViewPost)
        }

//        viewModel.postText.observe(viewLifecycleOwner) { text ->
//            binding.editTextPostContent.setText(text)
//        }
        viewModel.postText.observe(viewLifecycleOwner) { text ->
            if (binding.editTextPostContent.text.toString() != text) {
                binding.editTextPostContent.setText(text)
            }
        }

    }
    private fun saveUpdatedPost() {
        val newTextContent = binding.editTextPostContent.text.toString()
        val newImageUrl = viewModel.imageUrl.value ?: ""


        viewModel.updatePost(postId, newTextContent, newImageUrl,oldImageUrl)
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1000
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




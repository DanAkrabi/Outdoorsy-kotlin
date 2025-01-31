package com.example.outdoorsy.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.text
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
//import androidx.glance.visibility
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.outdoorsy.R
import com.example.outdoorsy.adapters.CommentsAdapter
import com.example.outdoorsy.databinding.FragmentPostDetailsBinding
import com.example.outdoorsy.model.dao.CommentModel
import com.example.outdoorsy.model.dao.PostModel
import com.example.outdoorsy.viewmodel.CommentsViewModel
import com.example.outdoorsy.viewmodel.PostViewModel
import com.example.outdoorsy.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class PostDetailsFragment : Fragment() {

    private var _binding: FragmentPostDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: PostDetailsFragmentArgs by navArgs()
    private val commentsViewModel: CommentsViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()

    // Declare commentsAdapter as a property of the fragment
    private lateinit var commentsAdapter: CommentsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postViewModel.fetchPostDetails(args.post.postId)
        setupObservers()
        commentsViewModel.fetchComments(args.post.postId)
        setupInteractionListeners(args.post)
        userViewModel.getUserDetails(args.post.userId) { fullname, profileImg ->
            binding.textUsername.text = fullname ?: "Unknown User"
            Glide.with(binding.imageUserProfile.context)
                .load(profileImg ?: R.drawable.ic_profile_placeholder)
                .into(binding.imageUserProfile)
        }
        setupViews(args.post)


    }

    private fun setupViews(post: PostModel) {
        Glide.with(this)
            .load(post.imageUrl)
            .into(binding.imagePost)

        // Fetch user details using the ViewModel
        userViewModel.getUserDetails(post.userId) { fullname, profileImg ->
            binding.textUsername.text = fullname ?: "Unknown User"
            Glide.with(binding.imageUserProfile.context)
                .load(profileImg ?: R.drawable.ic_profile_placeholder)
                .into(binding.imageUserProfile)
        }



        binding.textCaption.text = post.textContent
        binding.commentsCount.text = "${post.commentsCount} Comments"
        binding.textTimestamp.text = post.timestamp.toString()
        binding.likesCount.text = "${post.likesCount} Likes"

    }



    private fun setupObservers() {


        commentsViewModel.comments.observe(viewLifecycleOwner) { comments ->
            Log.d("FragmentDebug", "Received ${comments.size} comments in Fragment")

            if (binding.recyclerViewComments.layoutManager == null) {
                binding.recyclerViewComments.layoutManager = LinearLayoutManager(requireContext())
            }

            binding.recyclerViewComments.adapter = CommentsAdapter(comments, userViewModel)
        }
        // ✅ Observe post updates to get the latest commentsCount --not to delete--
        postViewModel.post.observe(viewLifecycleOwner) { updatedPost ->
            binding.commentsCount.text = "${updatedPost.commentsCount} Comments" // ✅ Correctly updates from Firestore
            binding.likesCount.text = "${updatedPost.likesCount} Likes"
            commentsViewModel.fetchComments(args.post.postId)

        }


    }




    private fun setupInteractionListeners(post: PostModel) {
        binding.buttonToggleComments.setOnClickListener {
            binding.commentsContainer.visibility = if (binding.commentsContainer.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            binding.buttonToggleComments.text = if (binding.commentsContainer.visibility == View.VISIBLE) "Hide Comments" else "Show Comments"
        }

        binding.buttonPostComment.setOnClickListener {
            val content = binding.editTextComment.text.toString().trim()
            if (content.isNotEmpty()) {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

                val newComment = CommentModel(
                    userId = currentUserId, // ✅ Correctly store the actual user ID
                    content = content,
                    timestamp = Date()
                )
                postViewModel.addCommentToPost(post.postId, newComment)
                binding.editTextComment.text.clear()
            }

         }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



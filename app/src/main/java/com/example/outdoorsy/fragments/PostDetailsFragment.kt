package com.example.outdoorsy.fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.outdoorsy.R
import com.example.outdoorsy.adapters.CommentsAdapter
import com.example.outdoorsy.databinding.FragmentPostDetailsBinding
import com.example.outdoorsy.model.PostModel
import com.example.outdoorsy.model.CommentModel
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
    private val commentsViewModel: CommentsViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()
    private val args: PostDetailsFragmentArgs by navArgs()
//    private lateinit var post: PostModel

    private lateinit var commentsAdapter: CommentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSwipeRefresh()
        postViewModel.checkLikeStatus(args.post.postId, args.post.userId)
        val post = args.post  // ✅ Use SafeArgs directly
        binding.post = post
        postViewModel.fetchPostDetails(args.post.postId)

        setupObservers()

        commentsViewModel.fetchComments(args.post.postId)//fetching the post comments
        setupInteractionListeners(args.post)



        userViewModel.getUserDetails(args.post.userId) { fullname, profileImg ->
            binding.textUsername.text = fullname ?: "Unknown User"
            Glide.with(binding.imageUserProfile.context)
                .load(profileImg ?: R.drawable.ic_profile_placeholder)
                .into(binding.imageUserProfile)
        }
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == args.post.userId) {
            binding.editDeleteContainer.visibility = View.VISIBLE
        } else {
            binding.editDeleteContainer.visibility = View.GONE
        }
        setupViews(post)
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true

            postViewModel.fetchPostDetails(args.post.postId)
            commentsViewModel.fetchComments(args.post.postId) // Fetching the post comments

            setupInteractionListeners(args.post)

            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupViews(post: PostModel) {
        Log.d("PostDetailsFragment", "Post data: $post")

        Glide.with(this)
            .load(post.imageUrl)
            .into(binding.imagePost)

        binding.textCaption.text = post.textContent.ifEmpty { "No Description" }
        binding.commentsCount.text = "${post.commentsCount} Comments"
        binding.textTimestamp.text = post.timestamp.toString()
        binding.likesCount.text = "${post.likesCount} Likes"
    }


    private fun setupObservers() {


        binding.recyclerViewComments.layoutManager = LinearLayoutManager(requireContext())

        commentsAdapter = CommentsAdapter(emptyList(), userViewModel) { userId ->
            val action = PostDetailsFragmentDirections
                .actionPostDetailsFragmentToUserProfileFragment(userId)
            findNavController().navigate(action)
        }
        binding.recyclerViewComments.adapter = commentsAdapter

        commentsViewModel.comments.observe(viewLifecycleOwner) { comments ->
            Log.d("UI Debug", " Received ${comments.size} comments in Fragment")

            if (comments.isEmpty()) {
                Log.e("UI Debug", " No comments to display!")
                return@observe
            }

            Log.d("UI Debug", "✅ Updating adapter with ${comments.size} comments")
            commentsAdapter.updateComments(comments)
            binding.recyclerViewComments.post {
                binding.recyclerViewComments.invalidate()
            }

            if (binding.recyclerViewComments.visibility != View.VISIBLE) {
                Log.e("UI Debug", "🚨 RecyclerView is HIDDEN! Making it visible...")
                binding.recyclerViewComments.visibility = View.VISIBLE
            }
        }



        binding.buttonToggleComments.setOnClickListener {//toggle comments button
            if (binding.commentsContainer.visibility == View.VISIBLE) {
                binding.commentsContainer.visibility = View.GONE
                binding.buttonToggleComments.text = "Show Comments" // 🔥 Correctly updates text
            } else {
                binding.commentsContainer.visibility = View.VISIBLE
                binding.buttonToggleComments.text = "Hide Comments"
            }
        }
        postViewModel.isLikedByUser.observe(viewLifecycleOwner) { isLiked ->

            binding.buttonLike.setImageResource(
                if (isLiked) R.drawable.ic_like else R.drawable.ic_heart_outline
            )

        }

        binding.imageUserProfile.setOnClickListener {
                val userId = args.post.userId
                if (userId.isNotEmpty()) {
                    val action = PostDetailsFragmentDirections.actionPostDetailsFragmentToUserProfileFragment(userId)
                    findNavController().navigate(action)
                } else {
                    Log.e("PostDetailsFragment", "Error: userId is empty, cannot navigate")
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                }
            }


        postViewModel.post.observe(viewLifecycleOwner) { updatedPost ->
            if (updatedPost != null) {
                Log.d("PostDetailsFragment", "post.observe() - Updated post received: $updatedPost")

                val preservedPost = updatedPost.copy(
                    fullname = if (updatedPost.fullname.isNotEmpty()) updatedPost.fullname else binding.textUsername.text.toString()
                )

                binding.post = preservedPost
                binding.executePendingBindings()

                binding.commentsCount.text = "${preservedPost.commentsCount} Comments"
                binding.likesCount.text = "${preservedPost.likesCount} Likes"

                if (!preservedPost.textContent.isNullOrEmpty()) {
                    binding.textCaption.text = preservedPost.textContent
                } else {
                    binding.textCaption.text = "No Description"
                }
            }
        }

        postViewModel.likesCount.observe(viewLifecycleOwner) { updatedLikes ->

            binding.likesCount.text = "$updatedLikes Likes"
        }
    }

    private fun setupInteractionListeners(post: PostModel) {


        binding.buttonEditPost.setOnClickListener {
            navigateToEditPost(post)
        }
        binding.buttonDeletePost.setOnClickListener {
            postViewModel.deletePost(post.postId)
            Toast.makeText(requireContext(), "Post deleted", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }


        binding.buttonPostComment?.setOnClickListener {
            val content = binding.editTextComment?.text?.toString()?.trim()
            if (!content.isNullOrEmpty()) {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

                val newComment = CommentModel(
                    userId = currentUserId,
                    content = content,
                    timestamp = Date()
                )

                postViewModel.addCommentToPost(post.postId, newComment)
                binding.editTextComment?.text?.clear()  // ✅ FIX: Safe call `?.clear()`
                commentsViewModel.fetchComments(args.post.postId)
            } else {
                Toast.makeText(requireContext(), "Comment cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonLike.setOnClickListener {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            postViewModel.toggleLike(post.postId)
        }
    }


    private fun navigateToEditPost(post: PostModel) {
        val action = PostDetailsFragmentDirections.actionPostDetailsFragmentToEditPostFragment(
            postId = post.postId,
            textContent = post.textContent ?: "",
            imageUrl = post.imageUrl ?: ""
        )
        findNavController().navigate(action)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


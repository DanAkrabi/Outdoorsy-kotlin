

package com.example.outdoorsy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.outdoorsy.R
import com.example.outdoorsy.adapters.PostsAdapter
import com.example.outdoorsy.databinding.FragmentUserProfileBinding
import com.example.outdoorsy.model.PostModel
import com.example.outdoorsy.viewmodel.PostViewModel
import com.example.outdoorsy.viewmodel.UserProfileViewModel
import com.example.outdoorsy.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private val userProfileViewModel: UserProfileViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels() // Consider moving post-related operations to UserProfileViewModel if appropriate
    private val args: UserProfileFragmentArgs by navArgs()
    private lateinit var postsAdapter: PostsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonFollow.setOnClickListener {
            userProfileViewModel.toggleFollowUser(args.userId)
        }

        val userId = args.userId
        userProfileViewModel.fetchUserProfile(userId)
        postViewModel.fetchUserPosts(userId)

        setupRecyclerView()
        observeUserProfile()
        observeUserPosts()
        observeFollowingStatus()
    }

    private fun observeFollowingStatus() {
        userProfileViewModel.isFollowing.observe(viewLifecycleOwner) { isFollowing ->
            binding.buttonFollow.text = if (isFollowing) "Unfollow" else "Follow"
        }
    }

    private fun observeUserProfile() {
        userProfileViewModel.userProfile.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.textViewUserName.text = it.fullname
                binding.textViewUserBio.text = it.bio
                binding.textViewFollowersCount.text = it.followersCount.toString()
                binding.textViewFollowingCount.text = it.followingCount.toString()

                Glide.with(requireContext())
                    .load(it.profileImg)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(binding.imageViewProfile)
            }
        }

        userProfileViewModel.followersCount.observe(viewLifecycleOwner) { count ->
            binding.textViewFollowersCount.text = count.toString()
        }

        userProfileViewModel.followingCount.observe(viewLifecycleOwner) { count ->
            binding.textViewFollowingCount.text = count.toString()
        }
    }

    private fun setupRecyclerView() {
        postsAdapter = PostsAdapter(requireContext()) { post ->
            navigateToPostDetails(post)
        }
        binding.recyclerViewPosts.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = postsAdapter
        }
    }

    private fun navigateToPostDetails(post: PostModel) {
        val action = UserProfileFragmentDirections.actionProfileFragmentToPostDetailsFragment(post)
        findNavController().navigate(action)
    }

    private fun observeUserPosts() {
        postViewModel.posts.observe(viewLifecycleOwner) { posts ->
            postsAdapter.submitList(posts)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


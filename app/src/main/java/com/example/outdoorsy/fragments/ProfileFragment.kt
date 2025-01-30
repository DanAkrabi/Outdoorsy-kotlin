package com.example.outdoorsy.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.semantics.text
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.outdoorsy.R
import com.example.outdoorsy.adapters.PostsAdapter
import com.example.outdoorsy.databinding.FragmentProfileBinding
import com.example.outdoorsy.model.dao.PostModel
import com.example.outdoorsy.viewmodel.ProfileViewModel
import com.example.outdoorsy.viewmodel.UserViewModel
import com.example.outdoorsy.viewmodel.PostViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()
    private lateinit var postsAdapter: PostsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModels()
    }

    private fun setupRecyclerView() {
        postsAdapter = PostsAdapter(requireContext()) { post ->
            navigateToPostDetails(post)
        }
        binding.recyclerViewPosts.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerViewPosts.adapter = postsAdapter
    }

    private fun observeViewModels() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            profileViewModel.fetchUserData(it)
            profileViewModel.fetchUserPosts(it)
            profileViewModel.fetchFollowersAndFollowingCounts(it)
        }

        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.profileName.text = it.fullname
                binding.profileBio.text = it.bio ?: "No bio available"
                Glide.with(this).load(it.profileImg).into(binding.profileImage)
            }
        }

       profileViewModel.posts.observe(viewLifecycleOwner) { posts ->
            postsAdapter.submitList(posts)
        }

        profileViewModel.followersCount.observe(viewLifecycleOwner) { count ->
            binding.followersCount.text = count.toString()
        }

        profileViewModel.followingCount.observe(viewLifecycleOwner) { count ->
            binding.followingCount.text = count.toString()
        }
    }

    private fun navigateToPostDetails(post: PostModel) {
        val action = ProfileFragmentDirections.actionProfileFragmentToPostDetailsFragment(post)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





package com.example.outdoorsy.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.compose.ui.semantics.text
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.outdoorsy.MainActivity
import com.example.outdoorsy.R
import com.example.outdoorsy.adapters.PostsAdapter
import com.example.outdoorsy.databinding.FragmentProfileBinding
import com.example.outdoorsy.model.PostModel
import com.example.outdoorsy.viewmodel.ProfileViewModel
import com.example.outdoorsy.viewmodel.UserViewModel
import com.example.outdoorsy.viewmodel.PostViewModel
import com.example.outdoorsy.viewmodel.WeatherViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Callback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()
    private val weatherViewModel: WeatherViewModel by viewModels()


    private lateinit var postsAdapter: PostsAdapter



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModels()
        binding.progressBar.visibility = View.VISIBLE
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                postViewModel.clearAllRoomPosts()
                FirebaseAuth.getInstance().signOut() // Sign out the user

                // Navigate back to Login screen
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        binding.editProfileButton.setOnClickListener {
            // Ensure that the data is not null or provide defaults
            val fullname = profileViewModel.user.value?.fullname ?: "Default Name"
            val imageUrl = profileViewModel.user.value?.profileImg ?: ""

            // Create the action with the required arguments
            val action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(fullname, imageUrl)
            findNavController().navigate(action)
        }
        binding.weatherButton.setOnClickListener {
            val cityName = "Tel Aviv"  // Or get the city from SharedPreferences or a config
            val apiKey = "dc20172f8d61a2ab56998e21a59ca110"  // Use your actual OpenWeather API key
            weatherViewModel.getWeather(cityName, apiKey)  // Call the ViewModel to fetch weather
            findNavController().navigate(R.id.action_profileFragment_to_weatherFragment)
        }





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
                binding.progressBar.visibility = View.GONE
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
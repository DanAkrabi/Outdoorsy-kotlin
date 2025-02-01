package com.example.outdoorsy.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.outdoorsy.R
import com.example.outdoorsy.adapters.PostsAdapter
import com.example.outdoorsy.databinding.FragmentHomepageBinding
import com.example.outdoorsy.model.dao.PostModel
import com.example.outdoorsy.viewmodel.HomepageViewModel
import com.example.outdoorsy.viewmodel.PostViewModel
import com.example.outdoorsy.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomepageFragment : Fragment(R.layout.fragment_homepage) {

    private var _binding: FragmentHomepageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomepageViewModel by viewModels()
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var auth: FirebaseAuth
    private val userViewModel: UserViewModel by activityViewModels()
    private val postViewModel: PostViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomepageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding.recyclerViewDestinations.visibility = View.VISIBLE

        // Initialize PostsAdapter with the click listener
        postsAdapter = PostsAdapter(requireContext()) { post ->
            navigateToPostDetails(post)
        }

//        binding.recyclerViewDestinations.adapter = postsAdapter
        binding.recyclerViewDestinations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postsAdapter
        }
        // Observe and submit posts to the adapter

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            if (posts != null && posts.isNotEmpty()) {
                Log.d("HomepageFragment", "Posts loaded: ${posts.size}")
                postsAdapter.submitList(posts)
            } else {
                Log.e("HomepageFragment", "Error loading posts: No posts found")
            }
        }

        viewModel.fetchHomepagePosts()
        // Observe user data
//        userViewModel.user.observe(viewLifecycleOwner) { user ->
//            if (user != null) {
//                Log.d("HomepageFragment", "User loaded: ${user.fullname}")
//                binding.textWelcome.text = "Welcome, ${user.fullname}"
//            } else {
//                Log.d("HomepageFragment", "User is null or failed to load")
//                binding.textWelcome.text = "Welcome to Outdoorsy!"
//            }
//        }
    }

            private fun navigateToPostDetails(post: PostModel) {
        val action = HomepageFragmentDirections.actionHomepageFragmentToPostDetailsFragment(post)
        findNavController().navigate(action)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


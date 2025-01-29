package com.example.outdoorsy.fragments

import com.example.outdoorsy.viewmodel.SearchViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.outdoorsy.R
import com.example.outdoorsy.adapters.PostsAdapter
import com.example.outdoorsy.adapters.SearchAdapter
import com.example.outdoorsy.databinding.FragmentHomepageBinding
import com.example.outdoorsy.model.dao.PostModel
import com.example.outdoorsy.viewmodel.HomepageViewModel
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
    private lateinit var searchAdapter: SearchAdapter
    private val searchViewModel: SearchViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomepageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()



        // Initialize PostsAdapter with the click listener
        postsAdapter = PostsAdapter(requireContext()) { post ->
            navigateToPostDetails(post)
        }

        binding.recyclerViewDestinations.adapter = postsAdapter

        // Observe and submit posts to the adapter
//        viewModel.fetchHomepagePosts().observe(viewLifecycleOwner) { posts ->
//            if (posts != null) {
//                postsAdapter.submitList(posts)
//            }else {
//                Log.e("HomepageFragment", "Error loading posts: posts is null")
//            }
//        }
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            if (posts != null) {
                postsAdapter.submitList(posts)
            } else {
                Log.e("HomepageFragment", "Error loading posts: posts is null")
            }
        }
//        viewModel.fetchHomepagePosts()
        // Observe user data
        userViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Log.d("HomepageFragment", "User loaded: ${user.fullname}")
                binding.textWelcome.text = "Welcome, ${user.fullname}"
            } else {
                Log.d("HomepageFragment", "User is null or failed to load")
                binding.textWelcome.text = "Welcome to Outdoorsy!"
            }
        }
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


//package com.example.outdoorsy.fragments
//
//import DestinationsAdapter
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.ui.setupWithNavController
//import com.example.outdoorsy.R
//import com.example.outdoorsy.adapters.PostsAdapter
//import com.example.outdoorsy.databinding.FragmentHomepageBinding
//import com.example.outdoorsy.viewmodel.HomepageViewModel
//import com.example.outdoorsy.viewmodel.UserViewModel
//import com.google.android.material.bottomnavigation.BottomNavigationView
//import com.google.firebase.auth.FirebaseAuth
//
//class HomepageFragment : Fragment(R.layout.fragment_homepage) {
//
//    private var _binding: FragmentHomepageBinding? = null
//    private val binding get() = _binding!!
//    private val viewModel: HomepageViewModel by viewModels()
//    private lateinit var adapter: DestinationsAdapter
//    private lateinit var auth: FirebaseAuth
//    private val userViewModel: UserViewModel by activityViewModels()
//    private lateinit var postsAdapter: PostsAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentHomepageBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        auth = FirebaseAuth.getInstance()
////        setupBottomNavigationView()
//
//        postsAdapter = PostsAdapter(requireContext(), emptyList()) { post ->
//            // טיפול בלחיצה על פוסט
//            navigateToPostDetails(post)
//        }
//        binding.recyclerViewDestinations.adapter = postsAdapter
//
//        viewModel.fetchPosts().observe(viewLifecycleOwner) { posts ->
//            if (posts != null) {
//                postsAdapter.submitList(posts)
//            }
//        }
//        adapter = DestinationsAdapter()
//        binding.recyclerViewDestinations.adapter = adapter
//
//        viewModel.destinations.observe(viewLifecycleOwner) { destinations ->
//            adapter.submitList(destinations)
//        }
//
//        viewModel.fetchDestinations()
//        userViewModel.user.observe(viewLifecycleOwner) { user ->
//            if (user != null) {
//                Log.d("HomepageFragment", "User loaded: ${user.fullname}")
//                binding.textWelcome.text = "Welcome, ${user.fullname}"
//            } else {
//                Log.d("HomepageFragment", "User is null or failed to load")
//                binding.textWelcome.text = "Welcome to Outdoorsy!"
//            }
//        }
//
//
//    }
//
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

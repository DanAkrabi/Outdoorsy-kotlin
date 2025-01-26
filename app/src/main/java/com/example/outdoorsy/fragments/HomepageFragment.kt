package com.example.outdoorsy.fragments

import DestinationsAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.outdoorsy.R
import com.example.outdoorsy.adapters.PostsAdapter
import com.example.outdoorsy.databinding.FragmentHomepageBinding
import com.example.outdoorsy.viewmodel.HomepageViewModel
import com.example.outdoorsy.viewmodel.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomepageFragment : Fragment(R.layout.fragment_homepage) {

    private var _binding: FragmentHomepageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomepageViewModel by viewModels()
    private lateinit var adapter: DestinationsAdapter
    private lateinit var auth: FirebaseAuth
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var postsAdapter: PostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomepageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
//        setupBottomNavigationView()

        postsAdapter=PostsAdapter(requireContext(), emptyList())
        binding.recyclerViewDestinations.adapter = postsAdapter

        viewModel.fetchPosts().observe(viewLifecycleOwner) { posts ->
            if (posts != null) {
                postsAdapter.submitList(posts)
            }
        }
        adapter = DestinationsAdapter()
        binding.recyclerViewDestinations.adapter = adapter

        viewModel.destinations.observe(viewLifecycleOwner) { destinations ->
            adapter.submitList(destinations)
        }

        viewModel.fetchDestinations()
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

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
import com.example.outdoorsy.adapters.FeedPostsAdapter
import com.example.outdoorsy.adapters.PostsAdapter
import com.example.outdoorsy.databinding.FragmentHomepageBinding
import com.example.outdoorsy.model.PostModel
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
        val feedPostsAdapter = FeedPostsAdapter(
            requireContext(),
            userViewModel,
            onPostClicked = { post -> navigateToPostDetails(post) },
            onUserProfileClick = { userId -> navigateToUserProfile(userId) }
        )
        // Initialize PostsAdapter with the click listener
        postsAdapter = PostsAdapter(requireContext()) { post ->
            navigateToPostDetails(post)
        }
        binding.recyclerViewDestinations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = feedPostsAdapter
        }
        viewModel.fetchHomepagePosts() // ✅ Fetch posts before observing

        viewModel.feedPosts.observe(viewLifecycleOwner) { posts ->  // ✅ Observe _posts, not posts
            if (posts != null && posts.isNotEmpty()) {
                Log.d("HomepageFragment", "Posts loaded: ${posts.size}")
                feedPostsAdapter.submitList(posts)
            } else {
                Log.e("HomepageFragment", "Error loading posts: No posts found")
            }
        }
//        viewModel.posts.observe(viewLifecycleOwner) { posts ->
//            if (posts != null && posts.isNotEmpty()) {
//                Log.d("HomepageFragment", "Posts loaded: ${posts.size}")
////                postsAdapter.submitList(posts)
//                feedPostsAdapter.submitList(posts)
//            } else {
//                Log.e("HomepageFragment", "Error loading posts: No posts found")
//            }
//        }
//
//        viewModel.fetchHomepagePosts()

    }

    private fun navigateToPostDetails(post: PostModel) {
        val action = HomepageFragmentDirections.actionHomepageFragmentToPostDetailsFragment(post)
        findNavController().navigate(action)
    }

    private fun navigateToUserProfile(userId: String) {
        val action = HomepageFragmentDirections
            .actionHomepageFragmentToUserProfileFragment(userId)
        findNavController().navigate(action)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

//package com.example.outdoorsy.fragments
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.asFlow
//import androidx.lifecycle.lifecycleScope
//import androidx.lifecycle.repeatOnLifecycle
//import androidx.navigation.fragment.findNavController
//import androidx.paging.LoadState
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.outdoorsy.R
//import com.example.outdoorsy.adapters.FeedPostsAdapter
//import com.example.outdoorsy.adapters.PostsAdapter
//import com.example.outdoorsy.databinding.FragmentHomepageBinding
//import com.example.outdoorsy.model.PostModel
//import com.example.outdoorsy.viewmodel.HomepageViewModel
//import com.example.outdoorsy.viewmodel.PostViewModel
//import com.example.outdoorsy.viewmodel.UserViewModel
//import com.google.firebase.auth.FirebaseAuth
//import dagger.hilt.android.AndroidEntryPoint
//
//
//
//@AndroidEntryPoint
//class HomepageFragment : Fragment(R.layout.fragment_homepage) {
//
//    private var _binding: FragmentHomepageBinding? = null
//    private val binding get() = _binding!!
//    private val homepageViewModel: HomepageViewModel by activityViewModels()
//    private lateinit var postsAdapter: PostsAdapter
//    private lateinit var auth: FirebaseAuth
//    private val userViewModel: UserViewModel by activityViewModels()
//    private val postViewModel: PostViewModel by viewModels()
//
//
//
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
//
//        setupRecyclerView()
//        observeViewModel()
////        binding.recyclerViewDestinations.visibility = View.VISIBLE
//
////        postsAdapter = PostsAdapter(requireContext()) { post ->
////            navigateToPostDetails(post)
////        }
//
//
//    }
//
//            private fun navigateToPostDetails(post: PostModel) {
//        val action = HomepageFragmentDirections.actionHomepageFragmentToPostDetailsFragment(post)
//        findNavController().navigate(action)
//    }
//
//    private fun navigateToUserProfile(userId: String) {
//        val action = HomepageFragmentDirections
//            .actionHomepageFragmentToUserProfileFragment(userId)
//        findNavController().navigate(action)
//    }
//
//
//
//
//
//
//    private fun setupRecyclerView() {
//        postsAdapter = PostsAdapter(requireContext()) { post -> navigateToPostDetails(post) }
//
//        binding.recyclerViewDestinations.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = postsAdapter
//            setHasFixedSize(true)
//        }
//    }
//    private fun observeViewModel() {
//        homepageViewModel.posts.observe(viewLifecycleOwner) { posts ->
//            Log.d("HomepageFragment", "Received ${posts.size} posts")
//            postsAdapter.submitList(posts) // Update UI
//        }
//
//        postViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
//            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//            binding.swipeRefreshLayout.isRefreshing = isLoading
//        }
//
////        postViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
////            errorMessage?.let {
////                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
////            }
////        }
//    }
//
//
//    private fun fetchPosts() {
//        val userId = auth.currentUser?.uid
//        if (userId != null) {
//            homepageViewModel.fetchPosts(userId)
//        } else {
//            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//
//
//
//
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
//

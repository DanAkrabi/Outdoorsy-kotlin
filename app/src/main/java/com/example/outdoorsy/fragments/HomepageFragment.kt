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
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerViewDestinations.visibility = View.VISIBLE
        val feedPostsAdapter = FeedPostsAdapter(
            requireContext(),
            userViewModel,
            onPostClicked = { post -> navigateToPostDetails(post) },
            onUserProfileClick = { userId -> navigateToUserProfile(userId) }
        )
        postsAdapter = PostsAdapter(requireContext()) { post ->
            navigateToPostDetails(post)
        }
        binding.recyclerViewDestinations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = feedPostsAdapter
        }
        viewModel.fetchHomepagePosts()

        viewModel.feedPosts.observe(viewLifecycleOwner) { posts ->  // ✅ Observe _posts, not posts
            binding.progressBar.visibility = View.GONE

            if (posts != null && posts.isNotEmpty()) {
                Log.d("HomepageFragment", "Posts loaded: ${posts.size}")
                feedPostsAdapter.submitList(posts)
            } else {
                Log.e("HomepageFragment", "Error loading posts: No posts found")
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchHomepagePosts()

            binding.swipeRefreshLayout.isRefreshing = false
        }

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

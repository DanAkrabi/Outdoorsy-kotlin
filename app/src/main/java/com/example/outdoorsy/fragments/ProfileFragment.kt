package com.example.outdoorsy.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.outdoorsy.R
import com.example.outdoorsy.adapters.PostsAdapter
import com.example.outdoorsy.databinding.FragmentProfileBinding
import com.example.outdoorsy.model.dao.PostModel
import com.example.outdoorsy.viewmodel.UserViewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    private val userViewModel: UserViewModel by activityViewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore // Reference to Firestore
    private lateinit var postsAdapter: PostsAdapter // Adapter for posts

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        postsAdapter = PostsAdapter(requireContext(), emptyList())
        binding.recyclerViewPosts.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerViewPosts.adapter = postsAdapter


        // Observe the logged-in user
        userViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Log.d("ProfileFragment", "User loaded: ${user.fullname}")
                binding.profileName.text = user.fullname
                binding.profileEmail.text = user.email
                binding.profileBio.text = user.bio ?: "No bio available"

                // Load profile image
                user.profileImg?.let { imgUrl ->
                    Glide.with(this)
                        .load(imgUrl)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .into(binding.profileImage)
                }

                // Fetch posts for the current user
                fetchUserPosts(user.id)
            } else {
                Log.d("ProfileFragment", "User is null")
                binding.profileName.text = "Guest"
                binding.profileEmail.text = ""
                binding.profileBio.text = ""
            }
        }
    }

    private fun fetchUserPosts(userId: String) {
        db.collection("posts")
            .whereEqualTo("userId", userId) // Fetch posts by this user
            .orderBy("timestamp", Query.Direction.DESCENDING) // Optional: Sort by newest first
            .get()
            .addOnSuccessListener { querySnapshot ->
                val posts = querySnapshot.documents.mapNotNull { it.toObject(PostModel::class.java) }
                Log.d("ProfileFragment", "Fetched ${posts.size} posts")
                // Use the posts list (e.g., update the RecyclerView adapter)
                postsAdapter.submitList(posts)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching posts: ${exception.message}")
            }
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
//import android.widget.Button
//import androidx.appcompat.widget.Toolbar
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//import com.example.outdoorsy.R
//import com.example.outdoorsy.databinding.FragmentProfileBinding
//import androidx.fragment.app.activityViewModels
//import com.example.outdoorsy.adapters.PostsAdapter
//import com.example.outdoorsy.viewmodel.UserViewModel
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//
//class ProfileFragment : Fragment() {
//    private val userViewModel: UserViewModel by activityViewModels()
//    private var _binding: FragmentProfileBinding? = null
//    private val binding get() = _binding!!
//    private val postsAdapter by lazy { PostsAdapter(requireContext(), mutableListOf()) }
//    private val firestore = Firebase.firestore
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
////        return inflater.inflate(R.layout.fragment_profile, container, false)
//        _binding = FragmentProfileBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        userViewModel.user.observe(viewLifecycleOwner) { user ->
//            if (user != null) {
//                Log.d("ProfileFragment", "User loaded: ${user.fullname}")
//                // Update the UI with user details
//                binding.profileName.text = user.fullname
//                binding.profileEmail.text = user.email
//                binding.profileBio.text = user.bio ?: "No bio available"
//                // If profile image is available, load it using Glide or similar library
//                // Glide.with(this).load(user.profileImg).into(binding.profileImage)
//            } else {
//                Log.d("ProfileFragment", "User is null")
//                // Handle case where user is null (e.g., logout state)
//                binding.profileName.text = "Guest"
//                binding.profileEmail.text = ""
//                binding.profileBio.text = ""
//            }
//        }
//    }
//}

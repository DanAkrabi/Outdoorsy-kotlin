package com.example.outdoorsy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.outdoorsy.R
import com.example.outdoorsy.databinding.ItemFeedPostBinding
import com.example.outdoorsy.model.PostModel
import com.example.outdoorsy.viewmodel.UserViewModel

class FeedPostsAdapter(
    private val context: Context,
    private val userViewModel: UserViewModel,
    private val onPostClicked: (PostModel) -> Unit,
    private val onUserProfileClick: (String) -> Unit
) : ListAdapter<PostModel, FeedPostsAdapter.FeedPostViewHolder>(PostDiffCallback()) {  // ✅ ListAdapter, not PagingDataAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedPostViewHolder {
        val binding = ItemFeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedPostViewHolder, position: Int) {
        val post = getItem(position)  // ✅ Uses ListAdapter, no Paging3
        holder.bind(post)
    }

    inner class FeedPostViewHolder(private val binding: ItemFeedPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: PostModel) {
            // Load user profile image & username
            userViewModel.getUserDetails(post.userId) { fullname, profileImg ->
                binding.textUsername.text = fullname ?: "Unknown User"
                Glide.with(binding.imageUserProfile.context)
                    .load(profileImg ?: R.drawable.ic_profile_placeholder)
                    .into(binding.imageUserProfile)
            }

            // Load post image
            Glide.with(binding.imagePost.context)
                .load(post.imageUrl)
                .placeholder(R.drawable.ic_placeholder_image)
                .into(binding.imagePost)

            // Set likes and comments count
            binding.likesCount.text = "${post.likesCount} Likes"
            binding.commentsCount.text = "${post.commentsCount} Comments"

            // Navigate to post details on click
            binding.root.setOnClickListener { onPostClicked(post) }

            // Navigate to user profile when clicking profile image
            binding.imageUserProfile.setOnClickListener { onUserProfileClick(post.userId) }
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<PostModel>() {
        override fun areItemsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
            return oldItem == newItem
        }
    }
}

//package com.example.outdoorsy.adapters
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.example.outdoorsy.R
//import com.example.outdoorsy.databinding.ItemFeedPostBinding
//import com.example.outdoorsy.model.PostModel
//import com.example.outdoorsy.viewmodel.UserViewModel
//
//class FeedPostsAdapter(
//    private val context: Context,
//    private val userViewModel: UserViewModel,
//    private val onPostClicked: (PostModel) -> Unit,
//    private val onUserProfileClick: (String) -> Unit
//) : ListAdapter<PostModel, FeedPostsAdapter.FeedPostViewHolder>(PostDiffCallback()) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedPostViewHolder {
//        val binding = ItemFeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return FeedPostViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: FeedPostViewHolder, position: Int) {
//        val post = getItem(position)
//        holder.bind(post)
//    }
//
//    inner class FeedPostViewHolder(private val binding: ItemFeedPostBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(post: PostModel) {
//            // Load user profile image & username
//            userViewModel.getUserDetails(post.userId) { fullname, profileImg ->
//                binding.textUsername.text = fullname ?: "Unknown User"
//                Glide.with(binding.imageUserProfile.context)
//                    .load(profileImg ?: R.drawable.ic_profile_placeholder)
//                    .into(binding.imageUserProfile)
//            }
//
//            // Load post image
//            Glide.with(binding.imagePost.context)
//                .load(post.imageUrl)
//                .placeholder(R.drawable.ic_placeholder_image)
//                .into(binding.imagePost)
//
//            // Set likes and comments count
//            binding.likesCount.text = "${post.likesCount} Likes"
//            binding.commentsCount.text = "${post.commentsCount} Comments"
//
//            // Navigate to post details on click
//            binding.root.setOnClickListener { onPostClicked(post) }
//
//            // Navigate to user profile when clicking profile image
//            binding.imageUserProfile.setOnClickListener { onUserProfileClick(post.userId) }
//
//        }
//    }
//
//    class PostDiffCallback : DiffUtil.ItemCallback<PostModel>() {
//        override fun areItemsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
//            return oldItem.postId == newItem.postId
//        }
//
//        override fun areContentsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
//            return oldItem == newItem
//        }
//    }
//}

package com.example.outdoorsy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.outdoorsy.R
import com.example.outdoorsy.model.dao.PostModel

class PostsAdapter(
    private val context: Context,
    private val onPostClicked: (PostModel) -> Unit
) : ListAdapter<PostModel, PostsAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) // Get post from ListAdapter

        // Load post image using Glide
        Glide.with(context)
            .load(post.imageUrl)
            .placeholder(R.drawable.ic_placeholder_image)
            .into(holder.postImage)

        // Set click listener
        holder.itemView.setOnClickListener { onPostClicked(post) }
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.imagePost)
    }

    // DiffUtil to compare PostModel objects
    class PostDiffCallback : DiffUtil.ItemCallback<PostModel>() {
        override fun areItemsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
            // Compare unique IDs
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
            // Compare full content
            return oldItem == newItem
        }
    }
}

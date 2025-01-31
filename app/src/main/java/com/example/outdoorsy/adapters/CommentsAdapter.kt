package com.example.outdoorsy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.example.outdoorsy.R
import com.example.outdoorsy.databinding.ItemCommentBinding
import com.example.outdoorsy.model.dao.CommentModel
import com.example.outdoorsy.viewmodel.UserViewModel


import androidx.lifecycle.LifecycleOwner

class CommentsAdapter(
    private var comments: List<CommentModel>,
    private val userViewModel: UserViewModel
) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding, userViewModel)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    class CommentViewHolder(
        private val binding: ItemCommentBinding,
        private val userViewModel: UserViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: CommentModel) {
            binding.textCommentContent.text = comment.content
            binding.textTimestamp.text = comment.timestamp.toString()

            // ✅ Fetch user details for each comment
            userViewModel.getUserDetails(comment.userId) { fullname, profileImg ->
                binding.textUsername.text = fullname ?: "Anonymous"
                Glide.with(binding.imageUserProfile.context)
                    .load(profileImg ?: R.drawable.ic_profile_placeholder)
                    .into(binding.imageUserProfile)
            }
        }
    }
}




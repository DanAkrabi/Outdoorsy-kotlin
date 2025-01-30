package com.example.outdoorsy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.outdoorsy.R
import com.example.outdoorsy.databinding.ItemSearchResultBinding
import com.example.outdoorsy.model.dao.UserModel

class SearchAdapter(private val onUserClick: (UserModel) -> Unit) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private var userList: List<UserModel> = emptyList()

    fun submitList(newList: List<UserModel>) {
        userList = newList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchViewHolder(binding)
    }


    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size

    inner class SearchViewHolder(private val binding: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserModel) {
            binding.fullname.text = user.fullname
            Glide.with(binding.profileImage.context)
                .load(user.profileImg)
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(binding.profileImage)

            // Handle user click and navigate to UserProfileFragment
            binding.root.setOnClickListener {
                onUserClick(user) // 🔥 Pass user data to `SearchFragment`
            }
        }
    }

}

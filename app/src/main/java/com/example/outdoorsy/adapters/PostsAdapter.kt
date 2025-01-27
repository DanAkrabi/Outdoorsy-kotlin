package com.example.outdoorsy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.outdoorsy.R
import com.example.outdoorsy.model.dao.PostModel
class PostsAdapter(
    private val context: Context,
    private var posts: List<PostModel>,
    private val onPostClicked: (PostModel) -> Unit
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]


        Glide.with(context)
            .load(post.imageUrl)
            .placeholder(R.drawable.ic_placeholder_image)
            .into(holder.postImage)


        holder.itemView.setOnClickListener {
            onPostClicked(post)
        }
    }


    override fun getItemCount(): Int = posts.size

    fun submitList(newPosts: List<PostModel>) {
        posts = newPosts
        notifyDataSetChanged()
    }
    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.imagePost)
    }

}

//    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val profileImage: ImageView = itemView.findViewById(R.id.imageProfile)
//        private val username: TextView = itemView.findViewById(R.id.textUsername)
//        private val postImage: ImageView = itemView.findViewById(R.id.imagePost)
//        private val caption: TextView = itemView.findViewById(R.id.textCaption)
//        private val likesCount: TextView = itemView.findViewById(R.id.likesCount)
//        private val commentsCount: TextView = itemView.findViewById(R.id.commentsCount)
//
//
//        fun bind(post: PostModel) {
//            // Load profile image
//            Glide.with(itemView.context)
//                .load(post.imageUrl) // Change if you have a specific profile image URL
//                .placeholder(R.drawable.ic_profile_placeholder)
//                .into(profileImage)
//
//            // Set username and caption
//            username.text = post.userId // Replace with a proper username lookup
//            caption.text = post.textContent
//
//            // Load post image
//            Glide.with(itemView.context)
//                .load(post.imageUrl)
//                .placeholder(R.drawable.ic_placeholder_image)
//                .into(postImage)
//            caption.text = post.textContent
//            likesCount.text = "${post.likesCount} Likes"
//            commentsCount.text = "${post.commentsCount} Comments"
//        }
//
//    }

//class PostsAdapter(
//    context: Context, // URLs or resource IDs
//    private val postImages: List<String>
//) :
//    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {
//    private val context: Context = context
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view: View =
//            LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val imageUrl = postImages[position]
//        // Load image using Glide or Picasso
//        Glide.with(context).load(imageUrl).into(holder.imagePost)
//    }
//
//    override fun getItemCount(): Int {
//        return postImages.size
//    }
//
//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var imagePost: ImageView = itemView.findViewById(R.id.imagePost)
//    }
//}

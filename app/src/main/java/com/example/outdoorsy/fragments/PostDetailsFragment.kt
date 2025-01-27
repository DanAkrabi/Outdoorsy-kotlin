package com.example.outdoorsy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.outdoorsy.databinding.FragmentPostDetailsBinding

class PostDetailsFragment : Fragment() {

    private var _binding: FragmentPostDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = PostDetailsFragmentArgs.fromBundle(requireArguments())
        val post = args.post

        Glide.with(this)
            .load(post.imageUrl)
            .into(binding.imagePost)

        binding.textCaption.text = post.textContent
        binding.likesCount.text = "${post.likesCount} Likes"
        binding.commentsCount.text = "${post.commentsCount} Comments"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

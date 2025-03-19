package com.example.outdoorsy.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.compose.ui.semantics.text
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.outdoorsy.R
import com.example.outdoorsy.adapters.SearchAdapter
import com.example.outdoorsy.databinding.FragmentSearchBinding
import com.example.outdoorsy.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchAdapter = SearchAdapter { selectedUser ->
            navigateToUserProfile(selectedUser.id)
        }

        binding.recyclerViewSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }

        binding.searchView.setupWithSearchBar(binding.searchBar)

        // Set up the listener for the SearchView's text changes
        binding.searchView.editText.doOnTextChanged { text, _, _, _ ->
            searchViewModel.searchUsers(text.toString())
        }


        searchViewModel.searchResults.observe(viewLifecycleOwner) { users ->
            Log.d("Search", "Received ${users.size} users") // ✅ Debug log

            searchAdapter.submitList(users)
            binding.recyclerViewSearchResults.apply {
                visibility = if (users.isEmpty()) View.GONE else View.VISIBLE
            }
        }

    }

    private fun navigateToUserProfile(userId: String) {
        val action = SearchFragmentDirections.actionSearchFragmentToUserProfileFragment(userId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
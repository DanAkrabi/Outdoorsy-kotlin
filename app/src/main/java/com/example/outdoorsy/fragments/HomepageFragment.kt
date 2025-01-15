package com.example.outdoorsy.fragments

import DestinationsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.outdoorsy.R
import com.example.outdoorsy.databinding.FragmentHomepageBinding
import com.example.outdoorsy.viewmodel.HomepageViewModel
import com.example.outdoorsy.model.Destination

class HomepageFragment : Fragment(R.layout.fragment_homepage) {

    private var _binding: FragmentHomepageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomepageViewModel by viewModels()
    private lateinit var adapter: DestinationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomepageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView adapter
        adapter = DestinationsAdapter()
        binding.recyclerViewDestinations.adapter = adapter

        // Observe destinations LiveData
        viewModel.destinations.observe(viewLifecycleOwner) { destinations ->
            if (destinations != null) {
                adapter.submitList(destinations) // Use submitList for ListAdapter
            }
        }

        // Fetch destinations
        viewModel.fetchDestinations()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

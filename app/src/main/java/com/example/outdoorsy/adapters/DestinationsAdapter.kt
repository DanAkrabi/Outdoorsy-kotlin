package com.example.outdoorsy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.outdoorsy.R

class DestinationsAdapter :
    ListAdapter<Destination, DestinationsAdapter.ViewHolder>(DestinationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_destination, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = view.findViewById(R.id.textViewDestination)

        fun bind(destination: Destination) {
            nameTextView.text = destination.name
        }
    }

    class DestinationDiffCallback : DiffUtil.ItemCallback<Destination>() {
        override fun areItemsTheSame(oldItem: Destination, newItem: Destination): Boolean {
            return oldItem.id == newItem.id // Compare items by unique ID
        }

        override fun areContentsTheSame(oldItem: Destination, newItem: Destination): Boolean {
            return oldItem == newItem // Compare contents of items
        }
    }
}

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

//package com.example.outdoorsy.ui
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.outdoorsy.R
//import com.example.outdoorsy.adapters.Destination
//
//class DestinationsAdapter(private var data: List<Destination>) :
//    RecyclerView.Adapter<DestinationsAdapter.ViewHolder>() {
//
//    fun updateData(newData: List<Destination>) {
//        data = newData
//        notifyDataSetChanged()
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_destination, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(data[position])
//    }
//
//    override fun getItemCount(): Int = data.size
//
//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val nameTextView: TextView = itemView.findViewById(R.id.textViewDestinationName)
//        private val descriptionTextView: TextView = itemView.findViewById(R.id.textViewDestinationDescription)
//
//        fun bind(destination: Destination) {
//            nameTextView.text = destination.name
//            descriptionTextView.text = destination.description
//        }
//    }
//}

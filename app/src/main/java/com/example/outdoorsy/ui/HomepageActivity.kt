package com.example.outdoorsy.ui



import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.outdoorsy.R

class HomepageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage_activity)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDestinations)
        // Setup RecyclerView with an adapter to display content
    }
}

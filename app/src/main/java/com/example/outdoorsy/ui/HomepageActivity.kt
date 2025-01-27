package com.example.outdoorsy.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.outdoorsy.R
import com.example.outdoorsy.viewmodel.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomepageActivity : AppCompatActivity() {
    val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage_activity)

        // Get NavHostFragment instance
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        // Set up BottomNavigationView with NavController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_picture -> {
                    // Navigate to CameraFragment when camera button is selected
                    navController.navigate(R.id.navigation_camera)
                    true
                }
                else -> {
                    // Handle navigation to the selected fragment, ensuring we replace the fragment in view
                    if (!navController.popBackStack(item.itemId, false)) {
                        navController.navigate(item.itemId)
                    }
                    true
                }
            }
        }
    }
}

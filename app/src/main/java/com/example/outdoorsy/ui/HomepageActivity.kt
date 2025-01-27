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
import androidx.appcompat.widget.Toolbar
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class HomepageActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var navController: NavController // Declare NavController at the class level

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar) // Ensure you have a Toolbar in your layout
        setSupportActionBar(toolbar)

        // Get NavHostFragment instance and initialize NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        // Setup AppBarConfiguration with top-level destinations
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_home, R.id.navigation_profile, R.id.navigation_camera))
        setupActionBarWithNavController(navController, appBarConfiguration)

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

    override fun onSupportNavigateUp(): Boolean {
        // This method is called when the up button is pressed. Just call NavController's navigateUp method.
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
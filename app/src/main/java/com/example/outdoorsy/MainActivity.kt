package com.example.outdoorsy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.HomepageActivity
import com.example.outdoorsy.viewmodel.LoginViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        // Set up NavHostFragment for Navigation Component
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Optional: Hook up the navController to the ActionBar
        NavigationUI.setupActionBarWithNavController(this, navController)

        loginViewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginViewModel.LoginState.Success -> {
                    navigateToHomepage() // ✅ Navigate to Homepage
                }
                LoginViewModel.LoginState.Empty -> {
                    // 🚀 Do nothing, stay on loginFragment (it's the start destination)
                }
                is LoginViewModel.LoginState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                LoginViewModel.LoginState.Loading -> {
                    // Optional: Show a loading UI
                }
            }
        }


        // ✅ Check if user is logged in (triggers LiveData update)
        loginViewModel.checkIfUserIsLoggedIn()

    }

    override fun onSupportNavigateUp(): Boolean {
        // Handle navigation "up" button clicks
        val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    private fun navigateToHomepage() {
        val intent = Intent(this, HomepageActivity::class.java)
        startActivity(intent)
        finish() // ✅ Close MainActivity so the user can’t go back to login
    }


}



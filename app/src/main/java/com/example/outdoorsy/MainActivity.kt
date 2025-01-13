package com.example.outdoorsy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.outdoorsy.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Optional: Enable back button or navigation drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        // Set up NavHostFragment for Navigation Component
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Optional: Hook up the navController to the ActionBar
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Handle navigation "up" button clicks
        val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}




//package com.example.outdoorsy
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.outdoorsy.model.UserModel
//import com.example.outdoorsy.repository.UserRepository
//import com.example.outdoorsy.ui.HomepageActivity
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//
//class MainActivity : AppCompatActivity() {
////    private val userRepository = UserRepository(UserModel :userModel)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//       //firebase db implementation
//        val db = Firebase.firestore
//
//        // Create a new user with a first and last name
//        val user = hashMapOf(
//            "first" to "Ada",
//            "last" to "Lovelace",
//            "born" to 1815,
//        )
//
//// Add a new document with a generated ID
//        db.collection("users")
//            .add(user)
//            .addOnSuccessListener { documentReference ->
//                Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
//            }
//            .addOnFailureListener { e ->
//                Log.w("TAG", "Error adding document", e)
//            }
//        //----------
//
//
//        setContentView(R.layout.activity_main)
//
//        // Initialize UI Components with scoped functions for clarity
//        val loginEmail = findViewById<EditText>(R.id.loginEmail)
//        val loginPassword = findViewById<EditText>(R.id.loginPassword)
//        val buttonLogin = findViewById<Button>(R.id.logIn)
//        val buttonRegister = findViewById<Button>(R.id.register)
//
//
//        // Handle login logic
//        buttonLogin.setOnClickListener {
//            val email = loginEmail.text.toString()
//            val password = loginPassword.text.toString()
//            if (checkCredentials(email, password)) {
//                val userFullname = userRepository.getFullnameByEmailAndPassword(email, password);
//                if (userFullname != null) {
//                    navigateToHomepage(userFullname)
//                }
//            } else {
//                showLoginError()
//            }
//        }
//
//        // Navigate to the registration screen
//        buttonRegister.setOnClickListener {
//            navigateToRegisterActivity()
//        }
//    }
//
//    private fun checkCredentials(email: String, password: String): Boolean {
//        // Validate credentials; replace with secure check in production
//
//
//        return email == "dan@gmail.com" && password == "1234567"
//    }
//
//    private fun navigateToHomepage(userFullname:String) {
//        val intent = Intent(this, HomepageActivity::class.java)
//        intent.putExtra("FULL_NAME", userFullname)
//        startActivity(intent)
//        finish()  // Close login activity upon successful login
//    }
//
//    private fun showLoginError() {
//        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_LONG).show()
//    }
//
//    private fun navigateToRegisterActivity() {
//        val intent = Intent(this, RegisterActivity::class.java)
//        startActivity(intent)
//    }
//}







//
//package com.example.outdoorsy
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.outdoorsy.ui.HomepageActivity
//
//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // Initialize UI Components
//        val loginEmail = findViewById<EditText>(R.id.loginEmail)
//        val loginPassword = findViewById<EditText>(R.id.loginPassword)
//        val buttonLogin = findViewById<Button>(R.id.logIn)
//        val buttonRegister = findViewById<Button>(R.id.register)
//
//        // Set click listener for the login button
//        buttonLogin.setOnClickListener {
//            if (checkCredentials(loginEmail.text.toString(), loginPassword.text.toString())) {
//                // Navigate to HomepageActivity
//                val intent = Intent(this, HomepageActivity::class.java)
//                startActivity(intent)
//                finish() // Optional: close this activity
//            } else {
//                // Show error message
//                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_LONG).show()
//            }
//        }
//
//        // Navigate to RegisterActivity
//        buttonRegister.setOnClickListener {
//            // Navigate to the RegisterActivity
//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivity(intent)
//        }
//    }
//
//    private fun checkCredentials(email: String, password: String): Boolean {
//        // Here you check the credentials. For now, let's assume these are correct:
//        // In a real app, you would check against a secure data source
//        return email == "user@example.com" && password == "password"
//    }
//}

package com.example.outdoorsy
import android.os.Bundle
import android.widget.Button
import android.content.Intent
//import androidx.activity.ComponentActivity

import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val buttonRegister = findViewById<Button>(R.id.button2)
        buttonRegister.setOnClickListener {
            // Navigate to the RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


}
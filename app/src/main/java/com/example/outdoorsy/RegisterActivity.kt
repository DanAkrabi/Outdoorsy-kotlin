package com.example.outdoorsy

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.outdoorsy.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        val emailInput = findViewById<EditText>(R.id.registerEmail)
        val passwordInput = findViewById<EditText>(R.id.registerPassword)
        val confirmPasswordInput = findViewById<EditText>(R.id.verifyRegisterPassword)
        val signUpButton = findViewById<Button>(R.id.SignUpButton)

        signUpButton.setOnClickListener {
            viewModel.registerUser(
                emailInput.text.toString(),
                passwordInput.text.toString(),
                confirmPasswordInput.text.toString()
            )
        }
    }
}

package com.example.outdoorsy.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.outdoorsy.R
import com.example.outdoorsy.databinding.FragmentLoginBinding
import com.example.outdoorsy.ui.HomepageActivity
import com.example.outdoorsy.viewmodel.LoginViewModel
import com.example.outdoorsy.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        // Navigate to RegisterFragment when Register button is clicked
        binding.register.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.logIn.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            Log.d("LoginViewModel", "Attempting to log in")
            // Validate inputs
            if (email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    viewModel.loginUser(email, password)
                }
            } else {
                Log.d("LoginViewModel", "Login failled, email or password are empty")
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT)
                    .show()
            }


        }

        // Observe the login state
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginViewModel.LoginState.Success -> {
                    val user = state.user
                    Toast.makeText(context, "Welcome, ${user.fullname}!", Toast.LENGTH_SHORT).show()
                    userViewModel.setUser(user)
                    // Navigate to HomepageActivity
                    navigateToHomePage() // Optional: Finish the current activity (e.g., login activity)
                }
                is LoginViewModel.LoginState.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
                LoginViewModel.LoginState.Loading -> {
                    // Show a loading spinner if applicable
                }
                LoginViewModel.LoginState.Empty -> {
                    // Do nothing
                }
            }
        }

    }private fun navigateToHomePage() {
//        val intent = Intent(requireContext(), HomepageActivity::class.java)
//        startActivity(intent)
//        requireActivity().finish()
        findNavController().navigate(R.id.action_loginFragment_to_homepageActivity)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


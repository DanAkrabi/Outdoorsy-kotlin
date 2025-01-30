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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
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

            // Validate inputs
            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.loginUser(email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe the login state
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginViewModel.LoginState.Success -> {
                    val user = state.user
                    // Set user data in the UserViewModel (for use in other fragments)
//                    userViewModel.setUser(user)//assigning the user obj from firestore
                    userViewModel.setUser(user)
                    Toast.makeText(context, "Welcome, ${user.fullname}!", Toast.LENGTH_SHORT).show()
                    navigateToHomePage() // Navigate to HomepageActivity
                }
                is LoginViewModel.LoginState.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
                LoginViewModel.LoginState.Loading -> {
                    // Show loading spinner if needed
                }
                LoginViewModel.LoginState.Empty -> {
                    // Do nothing
                }
            }
        }
    }

    private fun navigateToHomePage() {
        val intent = Intent(requireContext(), HomepageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()  // Close the current activity (LoginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



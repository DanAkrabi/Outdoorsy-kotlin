package com.example.outdoorsy.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.outdoorsy.R
import com.example.outdoorsy.databinding.FragmentRegisterBinding
import com.example.outdoorsy.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)
        binding.registerCancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.registerButton.setOnClickListener {
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            val fullname = binding.registerFullname.text.toString()

            viewModel.registerUser(email, password, confirmPassword, fullname)
        }

        viewModel.registrationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegisterViewModel.RegistrationState.Success -> {
                    binding.loader.visibility = View.GONE
                    Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
                is RegisterViewModel.RegistrationState.Error -> {
                    binding.loader.visibility = View.GONE
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
                RegisterViewModel.RegistrationState.Loading -> {
                    binding.loader.visibility = View.VISIBLE
                }
                RegisterViewModel.RegistrationState.Empty -> {
                    binding.loader.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



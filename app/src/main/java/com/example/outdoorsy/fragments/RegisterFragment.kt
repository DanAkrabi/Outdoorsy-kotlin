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

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)

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
                    Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
                is RegisterViewModel.RegistrationState.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
                RegisterViewModel.RegistrationState.Loading -> {
                    // Show a loading spinner (optional)
                }
                RegisterViewModel.RegistrationState.Empty -> {
                    // Do nothing
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



//
//package com.example.outdoorsy.fragments
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import com.example.outdoorsy.R
//import com.example.outdoorsy.databinding.FragmentRegisterBinding
//import com.example.outdoorsy.viewmodel.RegisterViewModel
//
//class RegisterFragment : Fragment() {
//
//    private var _binding: FragmentRegisterBinding? = null
//    private val binding get() = _binding!!
//    private val viewModel: RegisterViewModel by viewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Setup listeners
//        binding.registerButton.setOnClickListener {
//            val email = binding.registerEmail.text.toString()
//            val password = binding.registerPassword.text.toString()
//            val confirmPassword = binding.confirmPassword.text.toString()
//            val fullname=binding.registerFullname.text.toString()
//
//            viewModel.registerUser(email, password, confirmPassword,fullname)
//        }
//
//        binding.registerCancelButton.setOnClickListener {
//            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
//        }
//
//        // Observe registration state
//        viewModel.registrationState.observe(viewLifecycleOwner) { state ->
//            when (state) {
//                is RegisterViewModel.RegistrationState.Success -> {
//                    Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
//                    // Navigate to next screen or handle success
//                }
//                is RegisterViewModel.RegistrationState.Error -> {
//                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
//                }
//                RegisterViewModel.RegistrationState.Empty -> {
//                    // Initial state, do nothing
//                }
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

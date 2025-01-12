package com.example.outdoorsy.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel : ViewModel() {
    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Empty)
    val registrationState = _registrationState.asStateFlow()

    fun registerUser(email: String, password: String, confirmPassword: String) {
        // Validate inputs
        if (password != confirmPassword) {
            _registrationState.value = RegistrationState.Error("Passwords do not match")
            return
        }
        // Assume a function to handle registration
        // Result could be observed and state updated accordingly
        _registrationState.value = RegistrationState.Success
    }
}

sealed class RegistrationState {
    object Empty : RegistrationState()
    object Success : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}

package com.example.outdoorsy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.UserModel
import kotlinx.coroutines.launch
import com.example.outdoorsy.model.FirebaseModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterViewModel : ViewModel() {

    private val firestore= Firebase.firestore
    private val firebaseModel = FirebaseModel(firestore)

    private val _registrationState = MutableLiveData<RegistrationState>(RegistrationState.Empty)
    val registrationState: LiveData<RegistrationState> get() = _registrationState

    fun registerUser(email: String, password: String, confirmPassword: String, fullname: String) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || fullname.isEmpty()) {
            _registrationState.value = RegistrationState.Error("All fields are required")
            return
        }

        if (password != confirmPassword) {
            _registrationState.value = RegistrationState.Error("Passwords do not match")
            return
        }

        viewModelScope.launch {
            val user = UserModel(
                id = generateUserId(),
                email = email,
                password = password,
                fullname = fullname
            )
            val isSaved = firebaseModel.saveUser(user) // Use the instance
            if (isSaved) {
                _registrationState.value = RegistrationState.Success
            } else {
                _registrationState.value = RegistrationState.Error("Failed to register user.")
            }
        }
    }

    private fun generateUserId(): String {
        return java.util.UUID.randomUUID().toString()
    }

    sealed class RegistrationState {
        object Empty : RegistrationState()
        object Success : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }
}

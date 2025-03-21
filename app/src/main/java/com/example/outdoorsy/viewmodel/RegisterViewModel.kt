package com.example.outdoorsy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.FirebaseModel
import com.example.outdoorsy.model.CloudinaryModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseModel: FirebaseModel,
//    private val cloudinaryModel: CloudinaryModel
) : ViewModel() {
//    private val firebaseAuth = FirebaseAuth.getInstance()
//    private val firestore = Firebase.firestore
//    private val firebaseModel = FirebaseModel(firestore,firebaseAuth)

    private val _registrationState = MutableLiveData<RegistrationState>(RegistrationState.Empty)
    val registrationState: LiveData<RegistrationState> get() = _registrationState

    fun registerUser(email: String, password: String, confirmPassword: String, fullname: String) {
        // Validate inputs
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || fullname.isEmpty()) {
            _registrationState.value = RegistrationState.Error("All fields are required.")
            return
        }
        if (password != confirmPassword) {
            _registrationState.value = RegistrationState.Error("Passwords do not match.")
            return
        }
        if (password.length < 6) {
            _registrationState.value = RegistrationState.Error("Password must be at least 6 characters.")
            return
        }

        _registrationState.value = RegistrationState.Loading

        viewModelScope.launch {
            try {
                // Register the user using Firebase Authentication
//                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

                val authResult = firebaseModel.registerUser(email,password,fullname)




                _registrationState.value = RegistrationState.Success
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error("Registration failed: ${e.message}")
            }
        }
    }

    sealed class RegistrationState {
        object Empty : RegistrationState()
        object Loading : RegistrationState()
        object Success : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }
}

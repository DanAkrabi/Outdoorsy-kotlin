package com.example.outdoorsy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.dao.FirebaseModel
import com.example.outdoorsy.model.dao.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class RegisterViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore
    private val firebaseModel = FirebaseModel(firestore)

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
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

                // Save additional user information to Firestore
                val userId = authResult.user?.uid ?: UUID.randomUUID().toString()
                val user = UserModel(
                    id = userId,
                    email = email,
                    fullname = fullname,
                    password = password // Don't save plain-text passwords in production apps
                )
                firebaseModel.saveUser(user)

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





//package com.example.outdoorsy.viewmodel
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.outdoorsy.model.dao.UserModel
//import kotlinx.coroutines.launch
//import com.example.outdoorsy.model.dao.FirebaseModel
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//
//class RegisterViewModel : ViewModel() {
//
//    private val firestore= Firebase.firestore
//    private val firebaseModel = FirebaseModel(firestore)
//
//    private val _registrationState = MutableLiveData<RegistrationState>(RegistrationState.Empty)
//    val registrationState: LiveData<RegistrationState> get() = _registrationState
//
//    fun registerUser(email: String, password: String, confirmPassword: String, fullname: String) {
//        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || fullname.isEmpty()) {
//            _registrationState.value = RegistrationState.Error("All fields are required")
//            return
//        }
//
//        if (password != confirmPassword) {
//            _registrationState.value = RegistrationState.Error("Passwords do not match")
//            return
//        }
//
//        if(fullname.length<5){
//            _registrationState.value= RegistrationState.Error("name must be longer than 5 characters")
//        }
//        if(password.length<5){
//            _registrationState.value= RegistrationState.Error("password must be longer than 5 characters")
//        }
//
//        viewModelScope.launch {
//            val user = UserModel(
//                id = generateUserId(),
//                email = email,
//                password = password,
//                fullname = fullname
//            )
//            val isSaved = firebaseModel.saveUser(user) // Use the instance
//            if (isSaved) {
//                _registrationState.value = RegistrationState.Success
//            } else {
//                _registrationState.value = RegistrationState.Error("Failed to register user.")
//            }
//        }
//    }
//
//    private fun generateUserId(): String {
//        return java.util.UUID.randomUUID().toString()
//    }
//
//    sealed class RegistrationState {
//        object Empty : RegistrationState()
//        object Success : RegistrationState()
//        data class Error(val message: String) : RegistrationState()
//    }
//}

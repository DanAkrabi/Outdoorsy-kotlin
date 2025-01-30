package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.dao.FirebaseModel
import com.example.outdoorsy.model.dao.UserModel
import com.example.outdoorsy.repository.LoginRepository
import com.example.outdoorsy.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,

    private val loginRepository: LoginRepository
 // Inject your repository here
) : ViewModel() {

    private val firestore = Firebase.firestore

    // LiveData to observe login state
    private val _loginState = MutableLiveData<LoginState>(LoginState.Empty)
    val loginState: LiveData<LoginState> get() = _loginState

    fun loginUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginState.value = LoginState.Error("Email and password cannot be empty.")
            return
        }
        if (password.length < 5) {
            _loginState.value = LoginState.Error("Password must be longer than 5 characters")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val user = loginRepository.loginUser(email, password) // Use repository to login
            if (user != null) {
                _loginState.value = LoginState.Success(user)
            } else {
                _loginState.value = LoginState.Error("Login failed.")
            }
        }

    }



    // Sealed class to represent login state
    sealed class LoginState {
        object Empty : LoginState()
        object Loading : LoginState()
        data class Success(val user: UserModel) : LoginState() // Accept a UserModel instead of a String
        data class Error(val message: String) : LoginState()
    }
}



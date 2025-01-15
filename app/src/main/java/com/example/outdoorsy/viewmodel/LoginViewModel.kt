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

class LoginViewModel : ViewModel() {
    private val firestore = Firebase.firestore
    private val firebaseModel = FirebaseModel(firestore)

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
            try {
                // Authenticate user using Firebase Auth
                val authResult = FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email, password)
                    .await()

                val userId = authResult.user?.uid
                if (userId != null) {
                    // Fetch user details from Firestore using userId
                    val user = fetchUserDetails(userId)
                    if (user != null) {
                        _loginState.value = LoginState.Success(user) // Pass the entire user object
                    } else {
                        _loginState.value = LoginState.Error("User details not found in Firestore.")
                    }
                } else {
                    _loginState.value = LoginState.Error("Login failed. User ID is null.")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Login failed: ${e.message}")
            }
        }
    }

    private suspend fun fetchUserDetails(userId: String): UserModel? {
        return try {
            val documentSnapshot = Firebase.firestore.collection("users")
                .document(userId)
                .get()
                .await()

            documentSnapshot.toObject(UserModel::class.java)
        } catch (e: Exception) {
            throw Exception("Firebase error: ${e.message}")
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


//package com.example.outdoorsy.viewmodel
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.outdoorsy.model.dao.FirebaseModel
//import com.example.outdoorsy.model.dao.UserModel
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.firestore.local.QueryResult
//import com.google.firebase.ktx.Firebase
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//
//class LoginViewModel : ViewModel() {
//    private val firestore = Firebase.firestore
//    private val firebaseModel = FirebaseModel(firestore)
//
//    // LiveData to observe login state
//    private val _loginState = MutableLiveData<LoginState>(LoginState.Empty)
//    val loginState: LiveData<LoginState> get() = _loginState
//
//    fun loginUser(email: String, password: String) {
//        if (email.isEmpty() || password.isEmpty()) {
//            _loginState.value = LoginState.Error("Email and password cannot be empty.")
//            return
//        }
//        if(password.length<5){
//            _loginState.value=LoginState.Error("password must be longer than 5 characters")
//            return
//        }
//
//        _loginState.value = LoginState.Loading
//
//        viewModelScope.launch {
//            try {
//                val user = checkUserCredentials(email, password)
//                if (user != null) {
//                    _loginState.value = LoginState.Success(user) // Pass the entire user object
//                } else {
//                    _loginState.value = LoginState.Error("Invalid email or password.")
//                }
//            } catch (e: Exception) {
//                _loginState.value = LoginState.Error("Login failed: ${e.message}")
//            }
//        }
//    }
//
//    private suspend fun checkUserCredentials(email: String, password: String): UserModel? {
//        return try {
//            val querySnapshot = Firebase.firestore
//                .collection("users")
//                .whereEqualTo("email", email)
//                .whereEqualTo("password", password)
//                .get()
//                .await()
//
//            // If a document exists, map it to the UserModel object
//            if (querySnapshot.documents.isNotEmpty()) {
//                querySnapshot.documents[0].toObject(UserModel::class.java)
//            } else {
//                null // Credentials are invalid
//            }
//        } catch (e: Exception) {
//            throw Exception("Firebase error: ${e.message}")
//        }
//    }
//    // Sealed class to represent login state
//    sealed class LoginState {
//        object Empty : LoginState()
//        object Loading : LoginState()
//        data class Success(val user: UserModel) : LoginState() // Accept a UserModel instead of a String
//        data class Error(val message: String) : LoginState()
//    }
//
//}
//
//
///
package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.UserModel
import com.example.outdoorsy.model.dao.PostDao
import com.example.outdoorsy.model.dao.UserDao
import com.example.outdoorsy.repository.LoginRepository
import com.example.outdoorsy.repository.PostRepository
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
    private val userDao: UserDao,
    private val postDao: PostDao,
    private val loginRepository: LoginRepository,
    private val postRepository: PostRepository
 // Inject your repository here
) : ViewModel() {

    private val firestore = Firebase.firestore

    // LiveData to observe login state
    private val _loginState = MutableLiveData<LoginState>(LoginState.Empty)
    val loginState: LiveData<LoginState> get() = _loginState

    private val _cachedUser = MutableLiveData<UserModel?>()
    val cachedUser: LiveData<UserModel?> get() = _cachedUser


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
                userDao.clearUsers()
                postDao.clearAllPosts()
                userDao.insertUser(user)
                val freshPosts = postRepository.getUserPosts(user.id)
                postDao.insertPosts(freshPosts)
                _loginState.postValue(LoginState.Success(user)) // ✅ Notify UI

//                val cachedPosts = postDao.getUserPostsSync(user.id) // ✅ Check cache first
//                if (cachedPosts.isEmpty()) {
//                    Log.d("LoginViewModel", "⏳ No cached posts, fetching from Firebase...")
//                    postRepository.getUserPosts(user.id) // ✅ Only fetch if needed
//                }
                _loginState.value = LoginState.Success(user)

//                postRepository.getUserPosts(user.id)


            } else {
                _loginState.value = LoginState.Error("Login failed.")
            }
        }

    }
//    fun checkIfUserIsLoggedIn() {
//        val firebaseUser = FirebaseAuth.getInstance().currentUser
//        if (firebaseUser == null) {
//            _loginState.postValue(LoginState.Empty) // ✅ No user logged in
//            return
//        }
//
//        viewModelScope.launch {
//            val cachedUser = userDao.getUserByIdSync(firebaseUser.uid) // ✅ Fetch without affecting LiveData
//
//            if (cachedUser != null) {
//                _loginState.postValue(LoginState.Success(cachedUser)) // ✅ Load user without triggering UI conflicts
//            } else {
//                val firestoreUser = userRepository.getUserById(firebaseUser.uid)
//                if (firestoreUser != null) {
//                    userDao.insertUser(firestoreUser) // ✅ Cache user in Room
//                    _loginState.postValue(LoginState.Success(firestoreUser)) // ✅ Load user only after caching
//                }
//            }
//        }
//    }
        fun checkIfUserIsLoggedIn() {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser == null) {
                _loginState.postValue(LoginState.Empty) // No user logged in
                return
            }

            viewModelScope.launch {
                val cachedUser = userDao.getUserByIdSync(firebaseUser.uid) // ✅ Try cache first

                if (cachedUser != null) {
                    Log.d("LoginViewModel", "✅ Loaded cached user: ${cachedUser.fullname}")
                    _loginState.postValue(LoginState.Success(cachedUser))
                } else {
                    val firestoreUser = userRepository.getUserById(firebaseUser.uid)
                    if (firestoreUser != null) {
                        userDao.insertUser(firestoreUser) // ✅ Cache user
                        _loginState.postValue(LoginState.Success(firestoreUser))
                    }else{
                        _loginState.postValue(LoginState.Empty)
                    }
                }
            }
        }


    /**
     * **🚀 Step 3: Save the User in Room After Login**
     */



    // Sealed class to represent login state
    sealed class LoginState {
        object Empty : LoginState()
        object Loading : LoginState()
        data class Success(val user: UserModel) : LoginState() // Accept a UserModel instead of a String
        data class Error(val message: String) : LoginState()
    }
}



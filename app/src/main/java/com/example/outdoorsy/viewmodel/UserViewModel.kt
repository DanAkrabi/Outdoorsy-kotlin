package com.example.outdoorsy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.outdoorsy.model.dao.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserViewModel : ViewModel() {
    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> get() = _user

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(UserModel::class.java)
                    _user.value = user
                }
                .addOnFailureListener { exception ->
                    _user.value = null // Handle error
                }
        } else {
            _user.value = null // No user logged in
        }
    }
    fun setUser(user: UserModel) {
        _user.value = user
    }

    fun clearUser() {
        _user.value = null // Clear the user if needed (e.g., on logout)
    }
    fun refreshUserData() {
        fetchUserData() // Re-fetch user data on demand
    }
}

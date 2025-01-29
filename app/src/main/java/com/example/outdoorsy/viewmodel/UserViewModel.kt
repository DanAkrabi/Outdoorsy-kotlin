package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import com.example.outdoorsy.model.dao.UserModel
import com.example.outdoorsy.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> get() = _user

    private val _followersCount = MutableLiveData<Int>()
    val followersCount: LiveData<Int> get() = _followersCount

    private val _followingCount = MutableLiveData<Int>()
    val followingCount: LiveData<Int> get() = _followingCount

    // Fetch user by ID and update LiveData
    fun fetchUser(userId: String) {
        viewModelScope.launch {
            userRepository.getUserById(userId)?.let {
                _user.postValue(it)
            } ?: Log.e("UserViewModel", "Failed to fetch user")
        }
    }
    fun setUser(user: UserModel?) {
        if (user != null) {
            _user.value = user
            Log.d("UserViewModel", "User set: ${user.fullname}")
        } else {
            Log.e("UserViewModel", "Attempt to set null user")
        }
    }

    // Fetch and update followers count
    fun fetchFollowersCount(userId: String) {
        viewModelScope.launch {
            try {
                val count = userRepository.getFollowersCount(userId)
                _followersCount.postValue(count)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching followers count: ${e.message}")
            }
        }
    }

    // Fetch and update following count
    fun fetchFollowingCount(userId: String) {
        viewModelScope.launch {
            try {
                val count = userRepository.getFollowingCount(userId)
                _followingCount.postValue(count)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching following count: ${e.message}")
            }
        }
    }
}


//package com.example.outdoorsy.viewmodel
//
//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
////import androidx.hilt.lifecycle.ViewModelInject
//import androidx.lifecycle.viewModelScope
//import com.example.outdoorsy.model.dao.UserModel
//import com.example.outdoorsy.repository.UserRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class UserViewModel @Inject constructor(
//    private val userRepository: UserRepository
//// Hilt injects the repository here
//) : ViewModel() {
//
//    val _user = MutableLiveData<UserModel?>()
//    val user: LiveData<UserModel?> get() = _user
//
//    fun fetchUser(userId: String) {
//        viewModelScope.launch {
//            try {
//                val fetchedUser = userRepository.fetchUser(userId)
//                _user.postValue(fetchedUser)
//                Log.d("UserViewModel", "User fetched: ${fetchedUser?.fullname}")
//            } catch (e: Exception) {
//                Log.e("UserViewModel", "Error fetching user: ${e.message}")
//            }
//        }
//    }
//
//    private val _followersCount = MutableLiveData<Int>()
//    val followersCount: LiveData<Int> get() = _followersCount
//
//    private val _followingCount = MutableLiveData<Int>()
//    val followingCount: LiveData<Int> get() = _followingCount
//
//
//}



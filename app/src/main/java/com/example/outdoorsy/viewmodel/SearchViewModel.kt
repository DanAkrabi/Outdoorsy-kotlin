
package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.UserModel
import com.example.outdoorsy.repository.SearchRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _searchResults = MutableLiveData<List<UserModel>>()
    val searchResults: LiveData<List<UserModel>> get() = _searchResults

    fun searchUsers(query: String) {
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            return
        }

//        viewModelScope.launch {
//            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
//            val users = searchRepository.getUsersByName(query)
//
//            // ✅ Filter out the current logged-in user
//            val filteredUsers = users.filter { it.id != currentUserId }
//
//            _searchResults.postValue(filteredUsers)
//        }

        viewModelScope.launch {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            val users = searchRepository.getUsersByName(query)

            // ✅ Filter out the current logged-in user
            val filteredUsers = users.filter { it.id != currentUserId }

            Log.d("Search", "Updating LiveData with ${filteredUsers.size} users") // Debug log

            _searchResults.postValue(filteredUsers) // ✅ Make sure this updates LiveData
        }

    }

}


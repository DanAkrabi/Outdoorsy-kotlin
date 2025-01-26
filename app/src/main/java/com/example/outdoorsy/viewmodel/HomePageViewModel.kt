package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.model.Destination
import com.example.outdoorsy.model.dao.PostModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomepageViewModel : ViewModel() {
    // Backing property for destinations
    private val _destinations = MutableLiveData<List<Destination>>()
    val destinations: LiveData<List<Destination>> get() = _destinations

    // Backing property for error messages
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // Fetch destinations asynchronously
    fun fetchDestinations() {
        viewModelScope.launch {
            try {
                val querySnapshot = Firebase.firestore.collection("destinations").get().await()
                val fetchedDestinations = querySnapshot.documents.mapNotNull { it.toObject(Destination::class.java) }
                _destinations.postValue(fetchedDestinations)
                _error.postValue(null) // Clear any previous errors
            } catch (e: Exception) {
                _error.postValue("Failed to fetch destinations: ${e.message}")
            }
        }
    }
    fun fetchPosts(): LiveData<List<PostModel>> {
        val postsLiveData = MutableLiveData<List<PostModel>>()

        viewModelScope.launch {
            try {
                val posts = Firebase.firestore.collection("posts")
                    .get()
                    .await()
                    .toObjects(PostModel::class.java)
                postsLiveData.value = posts
            } catch (e: Exception) {
                Log.e("HomepageViewModel", "Error fetching posts: ${e.message}")
            }
        }

        return postsLiveData
    }

}

//package com.example.outdoorsy.viewmodel
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.example.outdoorsy.model.Destination
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import kotlinx.coroutines.tasks.await
//
//class HomepageViewModel : ViewModel() {
//    private val _destinations = MutableLiveData<List<Destination>?>()
//    val destinations: MutableLiveData<List<Destination>?> get() = _destinations
//
//    suspend fun fetchDestinations() {
//        try {
//            val querySnapshot = Firebase.firestore.collection("destinations").get().await()
//            val fetchedDestinations = querySnapshot.documents.mapNotNull { it.toObject(Destination::class.java) }
//            _destinations.postValue(fetchedDestinations)
//        } catch (e: Exception) {
//            _destinations.postValue(null) // Indicate an error
//        }
//    }
//}

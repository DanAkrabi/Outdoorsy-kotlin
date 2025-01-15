package com.example.outdoorsy.repository

import com.example.outdoorsy.model.dao.FirebaseModel
import com.example.outdoorsy.model.dao.UserModel

class UserRepository(private val firebaseModel: FirebaseModel) {
//    private val firebaseModel: FirebaseModel = FirebaseModel()
    // Create a new user in Firestore
    suspend fun createUser(user: UserModel) {
        firebaseModel.saveUser(user)
    }

    // Fetch a user from Firestore
    suspend fun fetchUser(userId: String): UserModel? {
        return firebaseModel.getUser(userId)
    }
}

//
//package com.example.outdoorsy.repository
//
//import com.example.outdoorsy.model.dao.FirebaseModel
//import com.example.outdoorsy.model.dao.UserModel
//
//class UserRepository(private val firebaseModel: FirebaseModel) {
//    fun registerUser(email: String, password: String): Boolean {
//        // Implement registration logic, possibly using Firebase or another backend service
//        return true
//    }
////    init {
////        // Automatically add some sample students
////        Users.add(Users(1,"dan@gmail.com","DanAkrabi","1234567"))
////        Users.add(Users(2,"danSalem@gmail.com","DanSalem","17547"))
////        Users.add(Users(3,"danMano@gmail.com","DanMano","127547"))
////
////    }
//
//
//    suspend fun createUser(user: UserModel) {
//       firebaseModel.saveUser(user)
//    }
//
//    suspend fun fetchUser(userId: String): UserModel? {
//        return firebaseModel.getUser(userId)
//    }
//
//    suspend fun modifyUser(userId: String, updatedData: Map<String, Any>) {
//        firebaseModel.updateUser(userId, updatedData)
//    }
//
//    suspend fun removeUser(userId: String) {
//        firebaseModel.deleteUser(userId)
//    }
//
////    fun getFullnameByEmailAndPassword(email: String, password: String): String? {
////        return Users.firstOrNull { it.email == email && it.password == password }?.fullName
////    }
//}

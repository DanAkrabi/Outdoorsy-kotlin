package com.example.outdoorsy.repository

import com.example.outdoorsy.Users

class UserRepository {
    private val Users = mutableListOf<Users>()
    fun registerUser(email: String, password: String): Boolean {
        // Implement registration logic, possibly using Firebase or another backend service
        return true
    }
    init {
        // Automatically add some sample students
        Users.add(Users(1,"dan@gmail.com","DanAkrabi","1234567"))
        Users.add(Users(2,"danSalem@gmail.com","DanSalem","17547"))
        Users.add(Users(3,"danMano@gmail.com","DanMano","127547"))

    }

    fun getFullnameByEmailAndPassword(email: String, password: String): String? {
        return Users.firstOrNull { it.email == email && it.password == password }?.fullName
    }
}

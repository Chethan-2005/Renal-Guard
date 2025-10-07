package com.example.renalguard

import java.io.Serializable

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: User? = null
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val role: String
) : Serializable   // ✅ Add this

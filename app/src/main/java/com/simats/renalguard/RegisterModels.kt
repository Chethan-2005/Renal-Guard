package com.simats.renalguard

// Request model for sending data to register.php
data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String
)

// Response model for receiving response from register.php
data class RegisterResponse(
    val success: Boolean,
    val message: String
)

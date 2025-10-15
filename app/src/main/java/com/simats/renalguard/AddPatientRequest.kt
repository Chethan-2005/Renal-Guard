package com.simats.renalguard

data class AddPatientRequest(
    val name: String,
    val age: String,
    val gender: String,
    val email: String,
    val phone: String,
    val doctor_email: String
)

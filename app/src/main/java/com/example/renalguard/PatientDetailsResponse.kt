package com.example.renalguard

data class PatientDetailsResponse(
    val success: Boolean,
    val message: String? = null,
    val data: PatientDetails? = null
)

data class PatientDetails(
    val patientId: String,
    val name: String,
    val age: Int,
    val gender: String,
    val email: String,
    val phone: String
)

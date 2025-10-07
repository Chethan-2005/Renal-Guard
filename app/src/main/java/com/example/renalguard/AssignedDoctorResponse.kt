package com.example.renalguard

data class AssignedDoctorResponse(
    val success: Boolean,
    val message: String? = null,
    val doctor: DoctorDetails? = null
)

data class DoctorDetails(
    val name: String,
    val specialization: String,
    val education: String,
    val location: String,
    val phone: String,
    val email: String
)

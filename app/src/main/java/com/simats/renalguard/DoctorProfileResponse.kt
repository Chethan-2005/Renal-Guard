package com.simats.renalguard.models

data class DoctorProfileResponse(
    val success: Boolean,
    val message: String? = null,
    val data: DoctorData? = null
)

data class DoctorData(
    val name: String,
    val phone: String,
    val email: String,
    val specialization: String,
    val education: String,
    val location: String
)

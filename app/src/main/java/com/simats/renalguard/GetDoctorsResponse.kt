package com.simats.renalguard

data class GetDoctorsResponse(
    val success: Boolean,
    val doctors: List<DoctorModel>
)

data class DoctorModel(
    val doctor_id: String,
    val name: String,
    val email: String,
    val phone: String,
    val specialization: String,
    val education: String,
    val location: String
)

package com.example.renalguard

import com.google.gson.annotations.SerializedName

data class PatientModel(
    @SerializedName("name") val name: String,
    @SerializedName("age") val age: Int,
    @SerializedName("gender") val gender: String,
    @SerializedName("patient_id") val patientId: String,
    @SerializedName("email") val patientEmail: String,   // 🔹 Add this
    val doctor_email: String? = null
)

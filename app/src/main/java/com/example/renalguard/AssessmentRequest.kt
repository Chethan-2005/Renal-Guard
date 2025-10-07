package com.example.renalguard

import com.google.gson.annotations.SerializedName

data class AssessmentRequest(
    @SerializedName("patient_id") val patient_id: String,
    @SerializedName("doctor_email") val doctor_email: String,
    @SerializedName("patient_email") val patient_email: String,
    @SerializedName("score") val score: Int,
    @SerializedName("line_duration") val line_duration: String,
    @SerializedName("stage") val stage: String
)

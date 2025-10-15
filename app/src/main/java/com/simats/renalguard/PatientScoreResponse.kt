package com.simats.renalguard

import com.google.gson.annotations.SerializedName

data class PatientScoreResponse(
    val success: Boolean,
    val message: String? = null,
    val scores: List<ScoreData>? = null
)

data class ScoreData(
    @SerializedName("score") val score: Int,
    @SerializedName("created_at") val createdAt: String
)

package com.simats.renalguard

import java.io.Serializable

data class DocModel(
    val doctor_id: String,
    val name: String,
    val email: String,
    val phone: String,
    val specialization: String,
    val education: String,
    val location: String
) : Serializable

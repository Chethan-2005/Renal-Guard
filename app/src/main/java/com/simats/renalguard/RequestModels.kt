package com.simats.renalguard

data class AddScheduleRequest(
    val doctor_email: String,
    val available_date: String,
    val start_time: String,
    val end_time: String,
    val slot_duration: Int,
    val max_patients: Int
)

data class DoctorIdRequest(val doctor_id: String)

data class ScheduleIdRequest(val schedule_id: Int)

data class BookAppointmentRequest(
    val schedule_id: Int,
    val patient_email: String,
    val slot_time: String
)

data class UpdateAppointmentRequest(
    val appointment_id: String,
    val patient_email: String,
    val status: String   // "attended" | "missed" | "cancelled"
)

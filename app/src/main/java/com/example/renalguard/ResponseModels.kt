package com.example.renalguard

data class BasicApiResponse(
    val success: Boolean,
    val message: String
)

data class DoctorSchedulesResponse(
    val success: Boolean,
    val message: String?,
    val schedules: List<DoctorSchedule>
)

data class DoctorSchedule(
    val schedule_id: String,
    val available_date: String,
    val start_time: String,
    val end_time: String,
    val slot_duration: String,
    val max_patients: String,
    val booked_count: String,
    val remaining: Int,
    val status: String,
    val patients: List<PatientMiniModel>
)

data class PatientMiniModel(
    val appointment_id: String,
    val slot_time: String,
    val status: String,
    val patient_name: String,
    val patient_email: String,
    val patient_phone: String
)



data class AvailableSlotsResponse(
    val success: Boolean,
    val message: String,
    val date: String,
    val slots: List<String>
)

data class DoctorAppointmentsResponse(
    val success: Boolean,
    val message: String,
    val doctor_id: String,
    val appointments: List<AppointmentModel>
)

data class AppointmentModel(
    val appointment_id: String,
    val schedule_id: String,
    val date: String,
    val slot_time: String,
    val appointment_status: String,
    val booking_date: String,
    val patient_email: String,
    val patient_name: String,
    val patient_phone: String,
    val schedule_start: String?,
    val schedule_end: String?
)

data class GeneralResponse(
    val success: Boolean,
    val message: String,
    val new_status: String? = null
)

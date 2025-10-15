package com.simats.renalguard

import AddPatientResponse
import BasicResponse
import PatientProfileResponse
import com.simats.renalguard.models.DoctorProfileResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

// Request classes
data class LoginRequest(val email: String, val password: String, val role: String, val fcm_token: String? = null)

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("login.php")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("register.php")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    // Send doctor_email in POST body as JSON
    @Headers("Content-Type: application/json")
    @POST("get_patients.php")
    fun getPatients(@Body request: com.simats.renalguard.DoctorEmailRequest): Call<List<PatientModel>>

    @Headers("Content-Type: application/json")
    @POST("search_patients.php")
    fun searchPatients(@Body request: DoctorEmailSearchRequest): Call<List<PatientModel>>

    @POST("add_patient.php")
    fun addPatient(@Body request: AddPatientRequest): Call<AddPatientResponse>


    @Headers("Content-Type: application/json")
    @POST("save_assessment.php")
    fun saveAssessment(
        @Body request: AssessmentRequest
    ): Call<ApiResponse>

    @POST("delete.php")
    fun deletePatient(@Body request: Map<String, String>): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("details.php")
    fun getPatientDetails(@Body request: Map<String, String>): Call<PatientDetailsResponse>

    @FormUrlEncoded
    @POST("update_patient.php")
    fun updatePatient(
        @Field("patient_id") patientId: String,
        @Field("name") name: String,
        @Field("age") age: String,
        @Field("gender") gender: String,
        @Field("email") email: String,
        @Field("mobile") phone: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("getDoctorProfile.php")
    fun getDoctorProfile(
        @Field("email") email: String
    ): Call<DoctorProfileResponse>

    @POST("updateDoctorProfile.php")
    fun updateDoctorProfile(@Body request: Map<String, String>): Call<GenericResponse>

    @POST("getAssignedDoctor.php")
    fun getAssignedDoctor(@Body request: Map<String, String>): Call<AssignedDoctorResponse>

    @Headers("Content-Type: application/json")
    @POST("getScores.php")
    fun getPatientScores(@Body request: Map<String, String>): Call<PatientScoreResponse>

    @Headers("Content-Type: application/json")
    @POST("getScoresById.php")
    fun getPatientScoresById(@Body request: Map<String, String>): Call<PatientScoreResponse>

    @POST("getPatientProfile.php")
    fun getPatientProfile(@Body body: RequestBody): Call<PatientProfileResponse>

    @POST("updatePatientProfile.php")
    fun updatePatientProfile(@Body body: RequestBody): Call<BasicResponse>

    @Headers("Content-Type: application/json")
    @POST("get_doctors.php")
    fun getDoctors(): Call<GetDoctorsResponse>

    @Headers("Content-Type: application/json")
    @POST("delete_doctor.php")
    fun deleteDoctor(@Body request: Map<String, String>): Call<GenericResponse>

    @POST("add_doctor.php")
    fun addDoctor(@Body body: Map<String, String>): Call<GenericResponse>

    @POST("updateAdminProfile.php")
    fun updateAdminProfile(@Body body: Map<String, String>): Call<GenericResponse>

    @Headers("Content-Type: application/json")
    @POST("addSchedule.php")
    fun addSchedule(@Body request: AddScheduleRequest): Call<BasicApiResponse>

    @Headers("Content-Type: application/json")
    @POST("get_doctor_schedules.php")
    fun getDoctorSchedules(@Body request: Map<String, String>): Call<DoctorSchedulesResponse>

    @Headers("Content-Type: application/json")
    @POST("getAvailableSlots.php")
    fun getAvailableSlots(@Body request: ScheduleIdRequest): Call<AvailableSlotsResponse>

    @Headers("Content-Type: application/json")
    @POST("bookAppointment.php")
    fun bookAppointment(@Body request: BookAppointmentRequest): Call<BasicApiResponse>

    @Headers("Content-Type: application/json")
    @POST("updateAppointment.php")
    fun updateAppointment(@Body request: UpdateAppointmentRequest): Call<GeneralResponse>

    @Headers("Content-Type: application/json")
    @POST("deleteAppointment.php")
    fun deleteAppointment(@Body request: UpdateAppointmentRequest): Call<GeneralResponse>

    data class DoctorEmailRequest(val doctor_email: String)

    @Headers("Content-Type: application/json")
    @POST("getDoctorAppointments.php")
    fun getDoctorAppointments(@Body request: Map<String, String>): Call<DoctorAppointmentsResponse>

    @Headers("Content-Type: application/json")
    @POST("update_fcm_token.php")
    fun updateFcmToken(@Body request: Map<String, String>): Call<BasicResponse>



}

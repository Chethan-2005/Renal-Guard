package com.example.renalguard

import AddPatientResponse
import GenericResponse
import com.example.renalguard.models.DoctorProfileResponse
import com.example.renalguard.models.PatientDetailsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

// Request classes
data class LoginRequest(val email: String, val password: String, val role: String)

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
    fun getPatients(@Body request: DoctorEmailRequest): Call<List<PatientModel>>

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

    @GET("getLatestScore.php")
    fun getLatestScore(
        @Query("patient_id") patientId: String
    ): Call<ScoreResponse>

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





}

package com.simats.renalguard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientDetailsDoctorActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etGender: EditText
    private lateinit var etEmail: EditText
    private lateinit var etMobile: EditText
    private lateinit var etPatientId: EditText
    private lateinit var btnSave: Button
    private lateinit var btnHome: ImageButton
    private lateinit var topAppBar: MaterialToolbar

    private var patientId: String? = null
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_details_doctor)

        // Initialize views
        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        etGender = findViewById(R.id.etGender)
        etEmail = findViewById(R.id.etEmail)
        etMobile = findViewById(R.id.etMobile)
        etPatientId = findViewById(R.id.etPatientId)
        btnSave = findViewById(R.id.btnSave)
        btnHome = findViewById(R.id.homeButton)
        topAppBar = findViewById(R.id.topAppBar)

        apiService = ApiClient.instance.create(ApiService::class.java)

        // Back arrow navigation
        topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Home button navigation
        btnHome.setOnClickListener {
            val intent = Intent(this, DoctorDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // Get patientId from intent
        patientId = intent.getStringExtra("patient_id")
        if (patientId.isNullOrEmpty()) {
            Toast.makeText(this, "No Patient ID passed", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Load patient details
        loadPatientDetails(patientId!!)

        // Save/Update button
        btnSave.setOnClickListener {
            updatePatient()
        }
    }

    private fun loadPatientDetails(patientId: String) {
        val request = mapOf("patientId" to patientId)
        Log.d("API_REQUEST", "Fetching details for $patientId")

        apiService.getPatientDetails(request)
            .enqueue(object : Callback<PatientDetailsResponse> {
                override fun onResponse(
                    call: Call<PatientDetailsResponse>,
                    response: Response<PatientDetailsResponse>
                ) {
                    Log.d("API_RESPONSE", "HTTP ${response.code()}")
                    val body = response.body()
                    if (response.isSuccessful && body?.success == true) {
                        val patient = body.data
                        patient?.let {
                            etName.setText(it.name)
                            etAge.setText(it.age.toString())
                            etGender.setText(it.gender)
                            etEmail.setText(it.email)
                            etMobile.setText(it.phone)
                            etPatientId.setText(it.patientId)
                        }
                    } else {
                        Log.e("API_RESPONSE", "API failed: ${body?.message}")
                        Toast.makeText(
                            this@PatientDetailsDoctorActivity,
                            "Failed to load details: ${body?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PatientDetailsResponse>, t: Throwable) {
                    Log.e("API_RESPONSE", "Failure: ${t.localizedMessage}", t)
                    Toast.makeText(
                        this@PatientDetailsDoctorActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun updatePatient() {
        val name = etName.text.toString().trim()
        val age = etAge.text.toString().trim()
        val gender = etGender.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etMobile.text.toString().trim()

        if (patientId.isNullOrEmpty() || name.isEmpty() || age.isEmpty() ||
            gender.isEmpty() || email.isEmpty() || phone.isEmpty()
        ) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("API_REQUEST", "Updating patient $patientId with name=$name, age=$age")

        apiService.updatePatient(patientId!!, name, age, gender, email, phone)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@PatientDetailsDoctorActivity,
                            "Patient updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Log.e("API_RESPONSE", "Update failed: ${response.errorBody()?.string()}")
                        Toast.makeText(
                            this@PatientDetailsDoctorActivity,
                            "Update failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.e("API_RESPONSE", "Update failure: ${t.localizedMessage}", t)
                    Toast.makeText(
                        this@PatientDetailsDoctorActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}

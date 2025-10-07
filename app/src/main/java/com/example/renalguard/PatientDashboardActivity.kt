package com.example.renalguard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientDashboardActivity : AppCompatActivity() {

    private lateinit var tvDoctorName: TextView
    private lateinit var tvDoctorQualification: TextView
    private lateinit var tvDoctorLocation: TextView
    private lateinit var imgDoctor: ImageView
    private lateinit var btnViewDoctor: Button
    private lateinit var btnScore: ImageButton
    private lateinit var btnBookAppointment: Button
    private lateinit var btnViewAppointments: Button


    private lateinit var navHome: ImageButton
    private lateinit var navInfo: ImageButton
    private lateinit var navProfile: ImageButton

    private lateinit var searchInput: TextInputEditText

    private var patientEmail: String = ""  // comes from LoginActivity
    private var assignedDoctorEmail: String = "" // doctor email to pass for booking

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)

        // Initialize views
        tvDoctorName = findViewById(R.id.tvDoctorName)
        tvDoctorQualification = findViewById(R.id.tvDoctorQualification)
        tvDoctorLocation = findViewById(R.id.tvDoctorLocation)
        imgDoctor = findViewById(R.id.imgDoctor)
        btnViewDoctor = findViewById(R.id.btnViewDoctor)
        btnScore = findViewById(R.id.btnScore)
        btnBookAppointment = findViewById(R.id.btnBookAppointment)
        btnViewAppointments = findViewById(R.id.btnViewAppointments)

        navHome = findViewById(R.id.navHome)
        navInfo = findViewById(R.id.navInfo)
        navProfile = findViewById(R.id.navProfile)

        searchInput = findViewById(R.id.searchInput)

        // Get patient email from LoginActivity
        patientEmail = intent.getStringExtra("patient_email") ?: ""
        if (patientEmail.isEmpty()) {
            Toast.makeText(this, "No patient email found", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch assigned doctor from backend
        fetchAssignedDoctor(patientEmail)

        // Score Button
        btnScore.setOnClickListener {
            val intent = Intent(this, PatientScoreResultActivity::class.java)
            intent.putExtra("patient_email", patientEmail)
            startActivity(intent)
        }

        // Book Appointment Button
        btnBookAppointment.setOnClickListener {
            if (assignedDoctorEmail.isEmpty()) {
                Toast.makeText(this, "No doctor assigned", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, BookAppointmentActivity::class.java)
                intent.putExtra("doctor_email", assignedDoctorEmail)   // pass doctor email
                intent.putExtra("patient_email", patientEmail)
                startActivity(intent)
            }
        }


        btnViewAppointments.setOnClickListener {
            val intent = Intent(this, PatientAppointmentsActivity::class.java)
            intent.putExtra("doctor_email", assignedDoctorEmail)  // ✅ pass assigned doctor email
            intent.putExtra("patient_email", patientEmail)
            startActivity(intent)
        }


        // Bottom navigation
        navHome.setOnClickListener {
            Toast.makeText(this, "You are already on Home", Toast.LENGTH_SHORT).show()
        }
        navInfo.setOnClickListener {
            startActivity(Intent(this, AboutPatientDietingActivity::class.java))
        }
        navProfile.setOnClickListener {
            val intent = Intent(this, PatientProfileActivity::class.java)
            intent.putExtra("patient_email", patientEmail)
            startActivity(intent)
        }
    }

    private fun fetchAssignedDoctor(email: String) {
        val service = ApiClient.instance.create(ApiService::class.java)
        val request = mapOf("email" to email)

        service.getAssignedDoctor(request).enqueue(object : Callback<AssignedDoctorResponse> {
            override fun onResponse(
                call: Call<AssignedDoctorResponse>,
                response: Response<AssignedDoctorResponse>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val doctor: DoctorDetails? = response.body()?.doctor
                    doctor?.let {
                        tvDoctorName.text = it.name
                        tvDoctorQualification.text = it.specialization
                        tvDoctorLocation.text = it.location
                        imgDoctor.setImageResource(R.drawable.ic_doctor)

                        // save doctor email for booking
                        assignedDoctorEmail = it.email

                        // View Doctor Details
                        btnViewDoctor.setOnClickListener { _ ->
                            val intent = Intent(
                                this@PatientDashboardActivity,
                                DoctorDetailsActivity::class.java
                            )
                            intent.putExtra("doctor_name", it.name)
                            intent.putExtra("doctor_qualification", it.specialization)
                            intent.putExtra("doctor_location", it.location)
                            intent.putExtra(
                                "doctor_details",
                                "Doctor Email: ${it.email}\nPhone: ${it.phone}\nEducation: ${it.education}"
                            )
                            intent.putExtra("doctor_image", R.drawable.ic_doctor)
                            startActivity(intent)
                        }
                    }
                } else {
                    Toast.makeText(
                        this@PatientDashboardActivity,
                        response.body()?.message ?: "No doctor assigned",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<AssignedDoctorResponse>, t: Throwable) {
                Log.e("PatientDashboard", "Error fetching doctor", t)
                Toast.makeText(
                    this@PatientDashboardActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}

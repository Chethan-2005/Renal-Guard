package com.simats.renalguard

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

    private var patientEmail: String = ""
    private var assignedDoctor: DoctorDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)

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

        patientEmail = intent.getStringExtra("patient_email") ?: ""
        if (patientEmail.isEmpty()) {
            Toast.makeText(this, "No patient email found", Toast.LENGTH_SHORT).show()
            return
        }

        fetchAssignedDoctor(patientEmail)

        btnScore.setOnClickListener {
            val intent = Intent(this, PatientScoreResultActivity::class.java)
            intent.putExtra("patient_email", patientEmail)
            startActivity(intent)
        }

        btnViewAppointments.setOnClickListener {
            val intent = Intent(this, PatientAppointmentsActivity::class.java)
            intent.putExtra("doctor_email", assignedDoctor?.email ?: "")
            intent.putExtra("patient_email", patientEmail)
            startActivity(intent)
        }

        btnBookAppointment.setOnClickListener {
            if (assignedDoctor == null) {
                Toast.makeText(this, "No doctor assigned", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, BookAppointmentActivity::class.java)
                intent.putExtra("doctor_email", assignedDoctor?.email)
                intent.putExtra("patient_email", patientEmail)
                startActivity(intent)
            }
        }

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

        // 🔍 Add live search
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (assignedDoctor == null) {
                    showNoDoctorAssigned()
                } else if (query.isEmpty()) {
                    displayDoctor(assignedDoctor!!)
                } else {
                    if (assignedDoctor!!.name.contains(query, ignoreCase = true)) {
                        displayDoctor(assignedDoctor!!)
                    } else {
                        showNoDoctorFound()
                    }
                }
            }
        })
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
                    val doctor = response.body()?.doctor
                    if (doctor != null) {
                        assignedDoctor = doctor
                        displayDoctor(doctor)
                    } else {
                        showNoDoctorAssigned()
                    }
                } else {
                    showNoDoctorAssigned()
                }
            }

            override fun onFailure(call: Call<AssignedDoctorResponse>, t: Throwable) {
                Log.e("PatientDashboard", "Error fetching doctor", t)
                showNoDoctorAssigned()
            }
        })
    }

    private fun displayDoctor(doctor: DoctorDetails) {
        tvDoctorName.text = doctor.name
        tvDoctorQualification.text = doctor.specialization
        tvDoctorLocation.text = doctor.location
        imgDoctor.setImageResource(R.drawable.ic_doctor)

        btnViewDoctor.setOnClickListener {
            val intent = Intent(this, DoctorDetailsActivity::class.java)
            intent.putExtra("doctor_name", doctor.name)
            intent.putExtra("doctor_qualification", doctor.specialization)
            intent.putExtra("doctor_location", doctor.location)
            intent.putExtra(
                "doctor_details",
                "Doctor Email: ${doctor.email}\nPhone: ${doctor.phone}\nEducation: ${doctor.education}"
            )
            intent.putExtra("doctor_image", R.drawable.ic_doctor)
            startActivity(intent)
        }
    }

    private fun showNoDoctorAssigned() {
        tvDoctorName.text = "No Doctor Assigned"
        tvDoctorQualification.text = "You will be assigned soon"
        tvDoctorLocation.text = ""
        imgDoctor.setImageResource(R.drawable.ic_doctor_placeholder)
        assignedDoctor = null

        btnViewDoctor.setOnClickListener {
            Toast.makeText(this, "Doctor not assigned yet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNoDoctorFound() {
        tvDoctorName.text = "No Doctor Found"
        tvDoctorQualification.text = ""
        tvDoctorLocation.text = ""
        imgDoctor.setImageResource(R.drawable.ic_doctor_placeholder)
    }
}

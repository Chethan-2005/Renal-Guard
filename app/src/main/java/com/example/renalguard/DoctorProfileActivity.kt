package com.example.renalguard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.renalguard.models.DoctorProfileResponse
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorProfileActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etMobile: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etSpecialization: TextInputEditText
    private lateinit var etEducation: TextInputEditText
    private lateinit var etLocation: TextInputEditText
    private lateinit var etChangePassword: TextInputEditText
    private lateinit var etReEnterPassword: TextInputEditText
    private lateinit var btnSaveExit: MaterialButton
    private lateinit var topAppBar: MaterialToolbar

    private var fromSource: String? = null
    private var doctorEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_profile)

        fromSource = intent.getStringExtra("source")
        doctorEmail = intent.getStringExtra("doctor_email") ?: ""

        etName = findViewById(R.id.etName)
        etMobile = findViewById(R.id.etMobile)
        etEmail = findViewById(R.id.etEmail)
        etSpecialization = findViewById(R.id.etSpecialization)
        etEducation = findViewById(R.id.etEducation)
        etLocation = findViewById(R.id.etLocation)
        etChangePassword = findViewById(R.id.etChangePassword)
        etReEnterPassword = findViewById(R.id.etReEnterPassword)
        btnSaveExit = findViewById(R.id.btnSaveExit)
        topAppBar = findViewById(R.id.topAppBar)

        // Load doctor profile
        if (doctorEmail.isNotEmpty()) {
            loadDoctorProfile(doctorEmail)
        } else {
            Toast.makeText(this, "Doctor email not found", Toast.LENGTH_SHORT).show()
        }

        // Info button → About Us
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, AboutUsActivity::class.java)
            intent.putExtra("source", "doctor")
            startActivity(intent)
        }

        // Menu (Theme & Logout)
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_theme -> {
                    toggleTheme()
                    true
                }
                R.id.action_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        // Save
        btnSaveExit.setOnClickListener { saveDoctorProfile() }
    }

    private fun loadDoctorProfile(email: String) {
        val api = ApiClient.instance.create(ApiService::class.java)
        api.getDoctorProfile(email).enqueue(object : Callback<DoctorProfileResponse> {
            override fun onResponse(
                call: Call<DoctorProfileResponse>,
                response: Response<DoctorProfileResponse>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data
                    data?.let {
                        etName.setText(it.name)
                        etMobile.setText(it.phone)
                        etEmail.setText(it.email)
                        etSpecialization.setText(it.specialization)
                        etEducation.setText(it.education)
                        etLocation.setText(it.location)
                    }
                } else {
                    Toast.makeText(
                        this@DoctorProfileActivity,
                        response.body()?.message ?: "Failed to load profile",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<DoctorProfileResponse>, t: Throwable) {
                Toast.makeText(this@DoctorProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveDoctorProfile() {
        val name = etName.text.toString().trim()
        val mobile = etMobile.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val specialization = etSpecialization.text.toString().trim()
        val education = etEducation.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val newPassword = etChangePassword.text.toString().trim()
        val rePassword = etReEnterPassword.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            return
        }

        val passwordToSend = if (newPassword.isNotEmpty() || rePassword.isNotEmpty()) {
            if (newPassword != rePassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return
            } else newPassword
        } else ""

        val api = ApiClient.instance.create(ApiService::class.java)
        val request = mutableMapOf(
            "email" to email,
            "name" to name,
            "phone" to mobile,
            "specialization" to specialization,
            "education" to education,
            "location" to location
        )

        if (passwordToSend.isNotEmpty()) {
            request["password"] = passwordToSend
        }

        api.updateDoctorProfile(request).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@DoctorProfileActivity, "Profile updated", Toast.LENGTH_SHORT).show()
                    navigateBack()
                } else {
                    Toast.makeText(
                        this@DoctorProfileActivity,
                        response.body()?.message ?: "Failed to update profile",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@DoctorProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateBack() {
        when (fromSource) {
            "dashboard" -> {
                val intent = Intent(this, DoctorDashboardActivity::class.java)
                intent.putExtra("doctor_email", doctorEmail)
                startActivity(intent)
                finish()
            }
            "addPatient" -> {
                val intent = Intent(this, AddPatientActivity::class.java)
                intent.putExtra("doctor_email", doctorEmail)
                startActivity(intent)
                finish()
            }
            else -> finish()
        }
    }

    private fun toggleTheme() {
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        recreate()
    }
}

package com.example.renalguard

import BasicResponse
import PatientProfileResponse
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etMobile: EditText
    private lateinit var etEmail: EditText
    private lateinit var etChangePassword: EditText
    private lateinit var etReEnterPassword: EditText
    private lateinit var btnSaveExit: Button
    private lateinit var btnInfo: ImageButton
    private lateinit var btnLogout: ImageButton
    private lateinit var themeToggle: ImageButton

    private var patientEmail = "" // Use email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_profile)

        // Bind views
        etName = findViewById(R.id.etName)
        etMobile = findViewById(R.id.etMobile)
        etEmail = findViewById(R.id.etEmail)
        etChangePassword = findViewById(R.id.etChangePassword)
        etReEnterPassword = findViewById(R.id.etReEnterPassword)
        btnSaveExit = findViewById(R.id.btnSaveExit)
        btnInfo = findViewById(R.id.btnInfo)
        btnLogout = findViewById(R.id.btnLogout)
        themeToggle = findViewById(R.id.themeToggle)

        updateThemeToggleIcon(themeToggle)

        // Get email from intent
        patientEmail = intent.getStringExtra("patient_email") ?: ""

        // Fetch patient profile from backend
        if (patientEmail.isNotEmpty()) fetchPatientProfile() else {
            Toast.makeText(this, "Patient email not provided", Toast.LENGTH_SHORT).show()
        }

        // Info button → About Us page
        btnInfo.setOnClickListener {
            val intent = Intent(this, AboutUsActivity::class.java)
            intent.putExtra("source", "patientProfile")
            startActivity(intent)
        }

        // Logout button → back to Login
        btnLogout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Theme Toggle
        themeToggle.setOnClickListener {
            val currentNightMode = AppCompatDelegate.getDefaultNightMode()
            if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            recreate()
        }

        // Save & Exit button
        btnSaveExit.setOnClickListener {
            updatePatientProfile()
        }
    }

    private fun fetchPatientProfile() {
        val json = JSONObject()
        json.put("email", patientEmail) // send email instead of ID

        val requestBody = json.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val api = ApiClient.instance.create(ApiService::class.java)
        api.getPatientProfile(requestBody).enqueue(object : Callback<PatientProfileResponse> {
            override fun onResponse(
                call: Call<PatientProfileResponse>,
                response: Response<PatientProfileResponse>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data
                    etName.setText(data?.name ?: "")
                    etEmail.setText(data?.email ?: "")
                    etMobile.setText(data?.phone ?: "")
                } else {
                    Toast.makeText(this@PatientProfileActivity, "Failed to fetch profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PatientProfileResponse>, t: Throwable) {
                Toast.makeText(this@PatientProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updatePatientProfile() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etMobile.text.toString().trim()
        val newPassword = etChangePassword.text.toString().trim()
        val rePassword = etReEnterPassword.text.toString().trim()

        val passwordToSend = if (newPassword.isNotEmpty() || rePassword.isNotEmpty()) {
            if (newPassword != rePassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return
            } else newPassword
        } else "" // keep empty if not updating

        val json = JSONObject()
        json.put("email", email) // send email instead of ID
        json.put("name", name)
        json.put("phone", phone)
        if (passwordToSend.isNotEmpty()) json.put("password", passwordToSend)

        val requestBody = json.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val api = ApiClient.instance.create(ApiService::class.java)
        api.updatePatientProfile(requestBody).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@PatientProfileActivity, "Profile saved", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@PatientProfileActivity, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                Toast.makeText(this@PatientProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateThemeToggleIcon(button: ImageButton) {
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            button.setImageResource(R.drawable.ic_light_mode)
            button.contentDescription = "Switch to Light Mode"
        } else {
            button.setImageResource(R.drawable.ic_dark_mode)
            button.contentDescription = "Switch to Dark Mode"
        }
    }
}

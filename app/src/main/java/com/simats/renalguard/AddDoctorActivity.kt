package com.simats.renalguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddDoctorActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPassword: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etSpecialization: EditText
    private lateinit var etEducation: EditText
    private lateinit var etLocation: EditText
    private lateinit var btnSaveDoctor: Button
    private lateinit var btnBackHome: Button
    private var adminEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_doctor)

        adminEmail = intent.getStringExtra("admin_email") ?: ""

        val topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            intent.putExtra("admin_email", adminEmail)
            startActivity(intent)
            finish()
        }

        // Initialize fields
        etName = findViewById(R.id.etName)
        etPassword = findViewById(R.id.etPassword)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etSpecialization = findViewById(R.id.etSpecialization)
        etEducation = findViewById(R.id.etEducation)
        etLocation = findViewById(R.id.etLocation)
        btnSaveDoctor = findViewById(R.id.btnSaveDoctor)
        btnBackHome = findViewById(R.id.btnBackHome)

        btnSaveDoctor.setOnClickListener { saveDoctor() }

        btnBackHome.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            intent.putExtra("admin_email", adminEmail)
            startActivity(intent)
            finish()
        }
    }

    private fun saveDoctor() {
        val name = etName.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val specialization = etSpecialization.text.toString().trim()
        val education = etEducation.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val role = "doctor"

        if (name.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val body = mapOf(
            "name" to name,
            "password" to password,
            "role" to role,
            "email" to email,
            "phone" to phone,
            "specialization" to specialization,
            "education" to education,
            "location" to location
        )

        ApiClient.instance.create(ApiService::class.java)
            .addDoctor(body)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@AddDoctorActivity,
                            "Doctor added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        clearFields()
                    } else {
                        Toast.makeText(
                            this@AddDoctorActivity,
                            response.body()?.message ?: "Failed to add doctor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddDoctorActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun clearFields() {
        etName.text.clear()
        etPassword.text.clear()
        etEmail.text.clear()
        etPhone.text.clear()
        etSpecialization.text.clear()
        etEducation.text.clear()
        etLocation.text.clear()
    }
}

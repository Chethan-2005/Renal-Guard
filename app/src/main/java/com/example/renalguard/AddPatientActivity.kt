package com.example.renalguard

import AddPatientResponse
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPatientActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etGender: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnNext: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var bottomNav: BottomNavigationView

    // Store logged-in doctor's email
    private lateinit var doctorEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)

        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        etGender = findViewById(R.id.etGender)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etMobile)
        btnNext = findViewById(R.id.btnNext)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE
        topAppBar = findViewById(R.id.topAppBar)
        bottomNav = findViewById(R.id.bottomNav)

        // Get doctor email from Intent
        doctorEmail = intent.getStringExtra("doctor_email") ?: ""

        // Back button in toolbar
        topAppBar.setNavigationOnClickListener {
            finish() // go back to previous activity
        }

        // Bottom Navigation
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, DoctorDashboardActivity::class.java)
                    intent.putExtra("doctor_email", doctorEmail)
                    startActivity(intent)
                    true
                }
                R.id.nav_add -> {
                    val intent = Intent(this, AddPatientActivity::class.java)
                    intent.putExtra("doctor_email", doctorEmail)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, DoctorProfileActivity::class.java)
                    intent.putExtra("doctor_email", doctorEmail)
                    intent.putExtra("source", "addPatient")
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        btnNext.setOnClickListener {
            addPatientToBackend()
        }
    }

    private fun addPatientToBackend() {
        val name = etName.text.toString().trim()
        val age = etAge.text.toString().trim()
        val gender = etGender.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()

        if (name.isEmpty() || age.isEmpty() || gender.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        val request = AddPatientRequest(
            name = name,
            age = age,
            gender = gender,
            email = email,
            phone = phone,
            doctor_email = doctorEmail
        )

        val api = ApiClient.instance.create(ApiService::class.java)
        api.addPatient(request).enqueue(object : Callback<AddPatientResponse> {
            override fun onResponse(
                call: Call<AddPatientResponse>,
                response: Response<AddPatientResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.success == true) {
                    val newPatientId = response.body()?.patient_id ?: ""
                    Toast.makeText(
                        this@AddPatientActivity,
                        response.body()?.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this@AddPatientActivity, AssessmentPatientActivity::class.java)
                    intent.putExtra("doctor_email", doctorEmail)
                    intent.putExtra("patient_id", newPatientId)
                    intent.putExtra("patient_email", email)
                    intent.putExtra("patient_age", age)   // ✅ send age
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(
                        this@AddPatientActivity,
                        response.body()?.message ?: "Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<AddPatientResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this@AddPatientActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}

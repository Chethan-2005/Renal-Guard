package com.simats.renalguard

import AddPatientResponse
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPatientActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var actvGender: AutoCompleteTextView
    private lateinit var etEmail: EditText
    private lateinit var etMobile: EditText
    private lateinit var btnNext: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var doctorEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)

        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        actvGender = findViewById(R.id.actvGender)
        etEmail = findViewById(R.id.etEmail)
        etMobile = findViewById(R.id.etMobile)
        btnNext = findViewById(R.id.btnNext)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE
        topAppBar = findViewById(R.id.topAppBar)

        doctorEmail = intent.getStringExtra("doctor_email") ?: ""

        // Back button
        topAppBar.setNavigationOnClickListener { finish() }

        // Gender dropdown
        val genders = arrayOf("Select Gender", "  Male", "  Female", "  Others")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genders)
        actvGender.setAdapter(genderAdapter)
        actvGender.setOnClickListener { actvGender.showDropDown() }

        btnNext.setOnClickListener { validateAndAddPatient() }
    }

    private fun validateAndAddPatient() {
        val name = etName.text.toString().trim()
        val ageStr = etAge.text.toString().trim()
        val gender = actvGender.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val mobile = etMobile.text.toString().trim()

        // Validations
        if (!name.matches(Regex("^[a-zA-Z ]+$"))) {
            etName.error = "Only letters allowed"
            return
        }

        val age = ageStr.toIntOrNull()
        if (age == null || age <= 1 || age > 150) {
            etAge.error = "Age must be 1-150"
            return
        }

        if (gender == "Select Gender" || gender.isEmpty()) {
            actvGender.error = "Select Gender"
            return
        } else {
            actvGender.error = null
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Invalid Email"
            return
        }

        if (!mobile.matches(Regex("\\d{10}"))) {
            etMobile.error = "Enter 10 digit mobile number"
            return
        }

        // Show progress
        progressBar.visibility = View.VISIBLE

        val request = AddPatientRequest(name, age.toString(), gender, email, mobile, doctorEmail)
        ApiClient.instance.create(ApiService::class.java).addPatient(request)
            .enqueue(object : Callback<AddPatientResponse> {
                override fun onResponse(call: Call<AddPatientResponse>, response: Response<AddPatientResponse>) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@AddPatientActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@AddPatientActivity, AssessmentPatientActivity::class.java)
                        intent.putExtra("doctor_email", doctorEmail)
                        intent.putExtra("patient_id", response.body()?.patient_id)
                        intent.putExtra("patient_email", email)
                        intent.putExtra("patient_age", age.toString())
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@AddPatientActivity, response.body()?.message ?: "Failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AddPatientResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@AddPatientActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

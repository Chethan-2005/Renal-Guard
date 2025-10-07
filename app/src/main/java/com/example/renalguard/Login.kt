package com.example.renalguard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private var toast: Toast? = null

    private fun showToast(message: String) {
        toast?.cancel()
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast?.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton: Button = findViewById(R.id.buttonLogin)
        val doctorRadio: RadioButton = findViewById(R.id.radioDoctor)
        val patientRadio: RadioButton = findViewById(R.id.radioPatient)
        val adminRadio: RadioButton = findViewById(R.id.radioAdmin)
        val emailEdit: EditText = findViewById(R.id.editTextDoctorId)
        val passwordEdit: EditText = findViewById(R.id.editTextPassword)
        val textSignup: TextView = findViewById(R.id.textSignup)
        val forgotPassword: Button = findViewById(R.id.btnForgotPassword)

        textSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        forgotPassword.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        loginButton.setOnClickListener {
            val email = emailEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()
            val role = when {
                doctorRadio.isChecked -> "doctor"
                patientRadio.isChecked -> "patient"
                adminRadio.isChecked -> "admin"
                else -> ""
            }

            if (email.isEmpty() || password.isEmpty() || role.isEmpty()) {
                showToast("All fields are required")
                return@setOnClickListener
            }

            val service = ApiClient.instance.create(ApiService::class.java)
            val request = LoginRequest(email, password, role)

            service.login(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val res = response.body()!!
                        if (res.success && res.user != null) {
                            val cleanEmail = res.user.email.trim()
                            Log.d("LoginDebug", "Logged in email: '$cleanEmail'")

                            showToast("Login Successful")

                            // Navigate to corresponding dashboard with email
                            val intent = when (role) {
                                "doctor" -> Intent(this@LoginActivity, DoctorDashboardActivity::class.java).apply {
                                    putExtra("doctor_email", cleanEmail)
                                }
                                "patient" -> Intent(this@LoginActivity, PatientDashboardActivity::class.java).apply {
                                    putExtra("patient_email", cleanEmail)
                                }
                                "admin" -> Intent(this@LoginActivity, AdminDashboardActivity::class.java).apply {
                                    putExtra("admin_email", cleanEmail)
                                }
                                else -> null
                            }

                            intent?.let {
                                startActivity(it)
                                finish()
                            }
                        } else {
                            showToast(res.message ?: "Invalid credentials")
                        }
                    } else {
                        Log.e("LoginError", "Server returned error: ${response.code()} - ${response.message()}")
                        showToast("Server Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("LoginError", "Network failure", t)
                    showToast("Network Error: ${t.message}")
                }
            })
        }
    }
}

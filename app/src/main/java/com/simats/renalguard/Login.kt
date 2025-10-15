package com.simats.renalguard

import BasicResponse
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
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

        textSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
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

            // ✅ Step 1: Get Firebase token
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    showToast("Unable to get FCM token")
                    return@addOnCompleteListener
                }

                val fcmToken = task.result
                Log.d("FCM_TOKEN", "Generated token: $fcmToken")

                val api = ApiClient.instance.create(ApiService::class.java)
                val request = LoginRequest(email, password, role, fcmToken)

                // ✅ Step 2: Call login.php
                api.login(request).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful && response.body() != null) {
                            val res = response.body()!!
                            if (res.success && res.user != null) {
                                val cleanEmail = res.user.email.trim()
                                showToast("Login Successful")

                                // ✅ Step 3: Send FCM token to backend
                                val tokenData = mapOf(
                                    "email" to cleanEmail,
                                    "fcm_token" to fcmToken
                                )

                                api.updateFcmToken(tokenData).enqueue(object : Callback<BasicResponse> {
                                    override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                                        Log.d("FCMUpdate", "Token updated successfully for $cleanEmail")
                                    }

                                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                                        Log.e("FCMUpdate", "Failed to update token: ${t.message}")
                                    }
                                })

                                // ✅ Step 4: Navigate to role-specific dashboard
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
                            showToast("Server error: ${response.code()}")
                            Log.e("LoginError", "Response error: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        showToast("Network Error: ${t.message}")
                        Log.e("LoginError", "Failure", t)
                    }
                })
            }
        }
    }
}

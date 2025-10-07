package com.example.renalguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProfileActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etChangePassword: EditText
    private lateinit var etReEnterPassword: EditText
    private lateinit var btnSaveChanges: Button
    private lateinit var btnLogout: Button
    private lateinit var topAppBar: MaterialToolbar

    private var adminEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_profile)

        etEmail = findViewById(R.id.etEmail)
        etChangePassword = findViewById(R.id.etChangePassword)
        etReEnterPassword = findViewById(R.id.etReEnterPassword)
        btnSaveChanges = findViewById(R.id.btnSaveChanges)
        btnLogout = findViewById(R.id.btnLogout)
        topAppBar = findViewById(R.id.topAppBar)

        // Get email from intent
        adminEmail = intent.getStringExtra("admin_email") ?: ""
        etEmail.setText(adminEmail)

        // Back arrow → Dashboard
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            intent.putExtra("admin_email", adminEmail)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_theme -> {
                    toggleTheme()
                    true
                }
                R.id.action_info -> {
                    val intent = Intent(this, AboutUsActivity::class.java)
                    intent.putExtra("source", "adminProfile")
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // Save password changes
        btnSaveChanges.setOnClickListener {
            updateAdminPassword()
        }

        // Logout → Login
        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun updateAdminPassword() {
        val newPassword = etChangePassword.text.toString().trim()
        val rePassword = etReEnterPassword.text.toString().trim()

        if (newPassword.isEmpty() || rePassword.isEmpty()) {
            Toast.makeText(this, "Please enter both password fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != rePassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val body = mapOf(
            "email" to adminEmail,
            "password" to newPassword
        )

        ApiClient.instance.create(ApiService::class.java)
            .updateAdminProfile(body)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@AdminProfileActivity,
                            "Password updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        etChangePassword.text.clear()
                        etReEnterPassword.text.clear()
                    } else {
                        Toast.makeText(
                            this@AdminProfileActivity,
                            response.body()?.message ?: "Failed to update password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AdminProfileActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
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

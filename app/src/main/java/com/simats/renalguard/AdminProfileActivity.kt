package com.simats.renalguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
    private lateinit var tvPasswordHint: TextView
    private lateinit var btnSaveChanges: Button
    private lateinit var btnLogout: Button
    private lateinit var topAppBar: MaterialToolbar

    private var adminEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_profile)

        // Bind views
        etEmail = findViewById(R.id.etEmail)
        etChangePassword = findViewById(R.id.etChangePassword)
        etReEnterPassword = findViewById(R.id.etReEnterPassword)
        tvPasswordHint = findViewById(R.id.tvPasswordHint)
        btnSaveChanges = findViewById(R.id.btnSaveChanges)
        btnLogout = findViewById(R.id.btnLogout)
        topAppBar = findViewById(R.id.topAppBar)

        // Get admin email from intent
        adminEmail = intent.getStringExtra("admin_email") ?: ""
        etEmail.setText(adminEmail)

        // Toolbar back arrow → Dashboard
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            intent.putExtra("admin_email", adminEmail)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        // Toolbar menu items
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

        // Save button → update password
        btnSaveChanges.setOnClickListener {
            updateAdminPassword()
        }

        // Logout button → Login
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

        // Password validation: minimum 8 chars, 1 number, 1 special char
        val passwordPattern = Regex("^(?=.*[0-9])(?=.*[!@#\$%^&*])[A-Za-z\\d!@#\$%^&*]{8,}\$")
        if (!passwordPattern.matches(newPassword)) {
            Toast.makeText(
                this,
                "Password must be at least 8 characters, include 1 number and 1 special character",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // Call API to update admin password
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

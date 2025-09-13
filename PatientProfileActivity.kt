package com.example.renalguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

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

        // Set initial theme icon
        updateThemeToggleIcon(themeToggle)

        // Info button → open About Us page
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
            recreate() // Recreate activity to apply theme changes
        }

        // Save & Exit button → back to Patient Dashboard
        btnSaveExit.setOnClickListener {
            // TODO: Save data logic here
            startActivity(Intent(this, PatientDashboardActivity::class.java))
            finish()
        }
    }

    private fun updateThemeToggleIcon(button: ImageButton) {
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            button.setImageResource(R.drawable.ic_light_mode) // Assuming you have an ic_light_mode drawable
            button.contentDescription = "Switch to Light Mode"
        } else {
            button.setImageResource(R.drawable.ic_dark_mode) // Assuming you have an ic_dark_mode drawable
            button.contentDescription = "Switch to Dark Mode"
        }
    }
}

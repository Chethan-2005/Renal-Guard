package com.example.renalguard

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class OtpVerificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        val btnChangePassword = findViewById<Button>(R.id.btnChangePassword)
        val btnDone = findViewById<Button>(R.id.btnDone)
        val textResendOtp = findViewById<TextView>(R.id.textResendOtp)

        // Set up toolbar navigation click (back to ResetPasswordActivity)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        btnChangePassword.setOnClickListener {
            // Navigate to ChangePasswordActivity
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        btnDone.setOnClickListener {
            // Navigate to Login page
            startActivity(Intent(this, LoginActivity::class.java))
        }

        textResendOtp.setOnClickListener {
            Toast.makeText(this, "OTP resent", Toast.LENGTH_SHORT).show()
        }
    }
}

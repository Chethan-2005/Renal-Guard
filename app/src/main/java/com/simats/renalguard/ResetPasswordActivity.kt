package com.simats.renalguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar


class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        val sendOtpBtn = findViewById<Button>(R.id.btnSendOtp)
        val emailEditText = findViewById<EditText>(R.id.editEmail)

        // Set up toolbar navigation click (back to Login)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Handle send OTP (you’ll connect this to backend later)
        sendOtpBtn.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            // You can add validations or mock OTP navigation here
            // For now, navigate to OtpVerificationActivity
            val intent = Intent(this, OtpVerificationActivity::class.java)
            startActivity(intent)
        }
    }
}

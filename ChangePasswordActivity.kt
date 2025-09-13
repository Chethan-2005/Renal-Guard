package com.example.renalguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val btnLogin = findViewById<Button>(R.id.btnLoginFromChangePassword)

        toolbar.setNavigationOnClickListener {
            finish() // Go back to previous activity (OTP Verification)
        }

        btnLogin.setOnClickListener {
            // You can validate password here if needed
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

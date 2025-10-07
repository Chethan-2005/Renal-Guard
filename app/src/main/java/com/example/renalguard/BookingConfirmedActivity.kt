package com.example.renalguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class BookingConfirmedActivity : AppCompatActivity() {

    private var patientEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_confirmed)

        patientEmail = intent.getStringExtra("patient_email") ?: ""

        val btnDone = findViewById<Button>(R.id.btnDone)
        btnDone.setOnClickListener {
            val intent = Intent(this, PatientDashboardActivity::class.java)
            intent.putExtra("patient_email", patientEmail)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}

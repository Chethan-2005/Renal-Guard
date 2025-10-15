package com.simats.renalguard

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class DoctorAppointmentOptionsActivity : AppCompatActivity() {

    private lateinit var cardScheduleAppointment: LinearLayout
    private lateinit var cardViewAppointments: LinearLayout
    private var doctorEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_appointment_options)

        doctorEmail = intent.getStringExtra("doctor_email") ?: ""

        // Toolbar setup
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // Cards
        cardScheduleAppointment = findViewById(R.id.cardScheduleAppointment)
        cardViewAppointments = findViewById(R.id.cardViewAppointments)

        // Open Schedule Form
        cardScheduleAppointment.setOnClickListener {
            val intent = Intent(this, AddScheduleActivity::class.java)
            intent.putExtra("doctor_email", doctorEmail)
            startActivity(intent)
        }

        // Open View Appointments
        cardViewAppointments.setOnClickListener {
            val intent = Intent(this, DoctorSchedulesActivity::class.java)
            intent.putExtra("doctor_email", doctorEmail)
            startActivity(intent)
        }
    }
}

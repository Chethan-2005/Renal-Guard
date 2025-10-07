package com.example.renalguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class DoctorDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_details)

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        val btnBackToHome: Button = findViewById(R.id.btnBackToHome)
        val imgDoctor: ImageView = findViewById(R.id.imgDoctor)
        val tvDoctorName: TextView = findViewById(R.id.tvDoctorName)
        val tvDoctorQualification: TextView = findViewById(R.id.tvDoctorQualification)
        val tvDoctorLocation: TextView = findViewById(R.id.tvDoctorLocation)
        val tvDoctorTiming: TextView = findViewById(R.id.tvDoctorTiming)
        val tvDoctorDetails: TextView = findViewById(R.id.tvDoctorDetails)

        // Get doctor data from intent
        val doctorName = intent.getStringExtra("doctor_name")
        val doctorQualification = intent.getStringExtra("doctor_qualification")
        val doctorLocation = intent.getStringExtra("doctor_location")
        val doctorTiming = intent.getStringExtra("doctor_timing")
        val doctorDetails = intent.getStringExtra("doctor_details")
        val doctorImageRes = intent.getIntExtra("doctor_image", R.drawable.ic_doctor)

        // Set UI values
        tvDoctorName.text = doctorName
        tvDoctorQualification.text = doctorQualification
        tvDoctorLocation.text = doctorLocation
        tvDoctorTiming.text = doctorTiming
        tvDoctorDetails.text = doctorDetails
        imgDoctor.setImageResource(doctorImageRes)

        // Toolbar back button
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Back to homepage button
        btnBackToHome.setOnClickListener {
            finish()
        }
    }
}

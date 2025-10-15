package com.simats.renalguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class DocDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doc_details)

        // Toolbar Back Button
        val topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        // ✅ Get doctor from Intent (Serializable)
        val doctor = intent.getSerializableExtra("doctor") as? DocModel

        // Views
        val imgDoctor: ImageView = findViewById(R.id.imgDoctor)
        val tvDoctorName: TextView = findViewById(R.id.tvDoctorName)
        val tvDoctorId: TextView = findViewById(R.id.tvDoctorId)
        val tvDoctorEmail: TextView = findViewById(R.id.tvDoctorEmail)
        val tvDoctorPhone: TextView = findViewById(R.id.tvDoctorPhone)
        val tvDoctorSpecialization: TextView = findViewById(R.id.tvDoctorSpecialization)
        val tvDoctorEducation: TextView = findViewById(R.id.tvDoctorEducation)
        val tvDoctorLocation: TextView = findViewById(R.id.tvDoctorLocation)
        val btnBackToHome: Button = findViewById(R.id.btnBackToHome)

        // ✅ Bind Data
        doctor?.let {
            tvDoctorName.text = "Dr. ${it.name}"
            tvDoctorId.text = "ID: ${it.doctor_id}"
            tvDoctorEmail.text = "Email: ${it.email}"
            tvDoctorPhone.text = "Phone: ${it.phone}"
            tvDoctorSpecialization.text = "Specialization: ${it.specialization}"
            tvDoctorEducation.text = "Education: ${it.education}"
            tvDoctorLocation.text = "Location: ${it.location}"
        }

        // ✅ Back to homepage button
        btnBackToHome.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}

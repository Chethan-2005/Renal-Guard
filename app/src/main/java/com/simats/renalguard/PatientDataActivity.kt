package com.simats.renalguard

import android.widget.Toast
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class PatientDataActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_data)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        val patientNameTextView: TextView = findViewById(R.id.patient_name)
        val patientEmailTextView: TextView = findViewById(R.id.patient_email) // ✅ added

        // Set up the Toolbar as ActionBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.title = "Patient Data"
        toolbar.setTitleTextColor(resources.getColor(R.color.white, theme))

        // Get values from Intent
        val patientId = intent.getStringExtra("patient_id")
        val patientName = intent.getStringExtra("patient_name")
        val doctorEmail = intent.getStringExtra("doctor_email")
        val patientEmail = intent.getStringExtra("patient_email")   // ✅ Get patient email

        // Show patient info
        patientNameTextView.text = "${patientName ?: "Unknown"} (ID: ${patientId ?: "N/A"})"
        patientEmailTextView.text = patientEmail ?: "No email available"  // ✅ Show email

        // Back arrow in toolbar → Go back to dashboard with patients
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, DoctorDashboardActivity::class.java)
            intent.putExtra("doctor_email", doctorEmail)
            startActivity(intent)
            finish()
        }

        // Back button at bottom
        findViewById<Button>(R.id.btn_back).setOnClickListener {
            val intent = Intent(this, DoctorDashboardActivity::class.java)
            intent.putExtra("doctor_email", doctorEmail)
            startActivity(intent)
            finish()
        }

        // Patient Details
        findViewById<Button>(R.id.btn_patient_details).setOnClickListener {
            val intent = Intent(this, PatientDetailsDoctorActivity::class.java)
            intent.putExtra("patient_id", patientId)   // ✅ pass correct ID
            startActivity(intent)
        }


        // ✅ Score Results (always open ScoreResultActivity)
        findViewById<Button>(R.id.btn_score_results).setOnClickListener {
            if (patientId != null) {
                val intent = Intent(this, ScoreResultActivity::class.java)
                intent.putExtra("patient_id", patientId) // ✅ only pass patient_id
                startActivity(intent)
            } else {
                Toast.makeText(this, "Patient ID missing!", Toast.LENGTH_SHORT).show()
            }
        }


        // Assessment
        findViewById<Button>(R.id.btn_assessment).setOnClickListener {
            val intent = Intent(this, AssessmentPatientActivity::class.java)
            intent.putExtra("patient_id", patientId)
            intent.putExtra("doctor_email", doctorEmail)
            intent.putExtra("patient_email", patientEmail)
            startActivity(intent)
        }
    }
}

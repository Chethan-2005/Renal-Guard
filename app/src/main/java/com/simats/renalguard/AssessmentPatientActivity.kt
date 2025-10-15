package com.simats.renalguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssessmentPatientActivity : AppCompatActivity() {

    private lateinit var api: ApiService
    private var patientId: String = ""
    private var doctorEmail: String = ""
    private var patientEmail: String = ""
    private var patientAge: Int = 0   // ✅ store patient age

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment_patient)

        api = ApiClient.instance.create(ApiService::class.java)

        // ✅ Get patient details from Intent
        patientId = intent.getStringExtra("patient_id") ?: ""
        doctorEmail = intent.getStringExtra("doctor_email") ?: ""
        patientEmail = intent.getStringExtra("patient_email") ?: ""
        patientAge = intent.getStringExtra("patient_age")?.toIntOrNull() ?: 0

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        val btnCalculate = findViewById<Button>(R.id.btnCalculate)

        // Back arrow → go to previous screen
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // ✅ Auto-select age question
        val ageAbove55 = findViewById<RadioButton>(R.id.ageAbove55)
        val ageBelow55 = findViewById<RadioButton>(R.id.ageBelow55)

        if (patientAge > 55) {
            ageAbove55.isChecked = true
        } else {
            ageBelow55.isChecked = true
        }

        // Disable editing so doctor cannot change manually
        ageAbove55.isEnabled = false
        ageBelow55.isEnabled = false

        btnCalculate.setOnClickListener {
            var score = 0

            // 1. Comorbidities
            if (findViewById<CheckBox>(R.id.diabetesCheckBox).isChecked) score += 1
            if (findViewById<CheckBox>(R.id.hypertensionCheckBox).isChecked) score += 1
            if (findViewById<CheckBox>(R.id.cancerCheckBox).isChecked) score += 1
            if (findViewById<CheckBox>(R.id.smokerCheckBox).isChecked) score += 1
            if (findViewById<CheckBox>(R.id.alcoholCheckBox).isChecked) score += 1
            if (findViewById<CheckBox>(R.id.otherDiseaseCheckBox).isChecked) score += 1

            // 2. Age (✅ now based on patientAge, not manual input)
            if (patientAge > 55) score += 2

            // 3. Multiple attempts
            if (findViewById<RadioButton>(R.id.attemptsYes).isChecked) score += 1

            // 4. Duration
            val lineDuration = when {
                findViewById<RadioButton>(R.id.week1).isChecked -> { score += 1; "Week 1" }
                findViewById<RadioButton>(R.id.week2).isChecked -> { score += 2; "Week 2" }
                findViewById<RadioButton>(R.id.week3).isChecked -> { score += 3; "Week 3" }
                else -> ""
            }

            // 5. Healthcare setup
            if (findViewById<RadioButton>(R.id.govtSetup).isChecked) score += 2
            else if (findViewById<RadioButton>(R.id.privateSetup).isChecked) score += 1

            // 6. Skin integrity
            if (findViewById<RadioButton>(R.id.skinInfected).isChecked) score += 1

            // 7. Line placement
            if (findViewById<RadioButton>(R.id.staffPlacement).isChecked) score += 1

            // 8. Dialysis frequency
            if (findViewById<RadioButton>(R.id.less3).isChecked) score += 1
            else if (findViewById<RadioButton>(R.id.less6).isChecked) score += 2
            else if (findViewById<RadioButton>(R.id.less9).isChecked) score += 3

            // 9. Chronic Steroid Use
            if (findViewById<RadioButton>(R.id.steroidYes).isChecked) score += 2

            // 10. Previous Antibiotic Use
            if (findViewById<RadioButton>(R.id.antibioticYes).isChecked) score += 1

            // 11. Post-line symptoms
            if (findViewById<RadioButton>(R.id.symptomYes).isChecked) score += 2

            // 12. Stage of AKI/CKD
            val stage = when {
                findViewById<RadioButton>(R.id.ckdStage).isChecked -> { score += 1; "CKD" }
                findViewById<RadioButton>(R.id.akiStage).isChecked -> "AKI"
                else -> ""
            }

            // ✅ Save assessment to server with patientEmail
            api.saveAssessment(
                AssessmentRequest(
                    patient_id = patientId,
                    doctor_email = doctorEmail,
                    patient_email = patientEmail,
                    score = score,
                    line_duration = lineDuration,
                    stage = stage
                )
            ).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val intent = Intent(this@AssessmentPatientActivity, ScoreActivity::class.java)
                        intent.putExtra("assessment_score", score)
                        intent.putExtra("line_duration", lineDuration)
                        intent.putExtra("stage", stage)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@AssessmentPatientActivity,
                            "Failed: ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@AssessmentPatientActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}

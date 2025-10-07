package com.example.renalguard

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientScoreResultActivity : AppCompatActivity() {

    private lateinit var rvScores: RecyclerView
    private lateinit var tvRecommendation: TextView
    private lateinit var btnBack: Button
    private lateinit var scoreAdapter: ScoreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_score_result)

        // Bind views
        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        rvScores = findViewById(R.id.rvScores)
        tvRecommendation = findViewById(R.id.tvRecommendation)
        btnBack = findViewById(R.id.btnBack)

        // Setup RecyclerView
        scoreAdapter = ScoreAdapter(emptyList())
        rvScores.layoutManager = LinearLayoutManager(this)
        rvScores.adapter = scoreAdapter

        // Toolbar back button
        toolbar.setNavigationOnClickListener { finish() }

        val patientEmail = intent.getStringExtra("patient_email")
        if (patientEmail.isNullOrEmpty()) {
            Toast.makeText(this, "Patient email missing!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchPatientScores(patientEmail)

        // Back to dashboard
        btnBack.setOnClickListener { finish() }
    }

    private fun fetchPatientScores(patientEmail: String) {
        val service = ApiClient.instance.create(ApiService::class.java)
        val request = mapOf("email" to patientEmail)

        service.getPatientScores(request).enqueue(object : Callback<PatientScoreResponse> {
            override fun onResponse(
                call: Call<PatientScoreResponse>,
                response: Response<PatientScoreResponse>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val scores = response.body()?.scores
                    if (!scores.isNullOrEmpty()) {
                        scoreAdapter.submitList(scores)

                        // Show recommendation for the latest score
                        val latest = scores.first()
                        showRecommendation(latest.score)
                    } else {
                        Toast.makeText(this@PatientScoreResultActivity, "No scores found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@PatientScoreResultActivity, "Failed: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PatientScoreResponse>, t: Throwable) {
                Log.e("PatientScore", "Error fetching scores", t)
                Toast.makeText(this@PatientScoreResultActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showRecommendation(score: Int) {
        val recommendation = when (score) {
            in 5..10 -> "Mild risk: No antibiotics unless symptoms appear (fever, swelling, discharge)."
            in 11..18 -> "Moderate risk: Doctor's discretion based on symptoms."
            in 19..25 -> "Severe risk: Strongly recommend antibiotics and catheter removal if possible."
            else -> "No recommendation available."
        }
        tvRecommendation.text = recommendation
    }
}

package com.example.renalguard

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class ScoreResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score_result)

        // Get data from intent
        val hasScore = intent.getBooleanExtra("hasScore", false)
        val score = intent.getIntExtra("score", 0)
        val date = intent.getStringExtra("date") ?: "--/--/----"
        val patientName = intent.getStringExtra("patientName") ?: "This patient"

        // Bind views
        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        val tvDate = findViewById<TextView>(R.id.tvDate)
        val tvScore = findViewById<TextView>(R.id.tvScore)
        val tvRecommendation = findViewById<TextView>(R.id.tvRecommendation)
        val btnBack = findViewById<Button>(R.id.btnBack)

        // Toolbar back arrow → just go back
        toolbar.setNavigationOnClickListener {
            finish()   // ✅ Close this activity and return
        }

        if (hasScore) {
            tvDate.text = date
            tvScore.text = score.toString()

            val recommendation = when (score) {
                in 5..10 -> "Mild risk: No antibiotics unless symptoms appear (fever, swelling, discharge)."
                in 11..18 -> "Moderate risk: Doctor's discretion based on symptoms."
                in 19..25 -> "Severe risk: Strongly recommend antibiotics and catheter removal if possible."
                else -> "No recommendation available."
            }
            tvRecommendation.text = recommendation
        } else {
            tvDate.text = "--/--/----"
            tvScore.text = "-"
            tvRecommendation.text = "⚠️ $patientName has not taken any assessment yet."
        }

        // Bottom back button → just go back
        btnBack.setOnClickListener {
            finish()   // ✅ Same here
        }
    }
}

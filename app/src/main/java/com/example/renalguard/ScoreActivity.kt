package com.example.renalguard

import android.content.Intent
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class ScoreActivity : AppCompatActivity() {

    private lateinit var tvPatientScore: TextView
    private lateinit var tvTotalScore: TextView
    private lateinit var tvRecommendations: TextView
    private lateinit var ivArrow: ImageView
    private lateinit var scaleBar: LinearLayout
    private lateinit var btnDone: Button

    private val totalScore = 25  // Max possible score

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        tvPatientScore = findViewById(R.id.tvPatientScore)
        tvTotalScore = findViewById(R.id.tvTotalScore)
        tvRecommendations = findViewById(R.id.tvRecommendations)
        ivArrow = findViewById(R.id.ivArrow)
        scaleBar = findViewById(R.id.scaleBar)
        btnDone = findViewById(R.id.btnDone)

        // Toolbar back
        toolbar.setNavigationOnClickListener { finish() }

        // Get data from intent
        val patientScore = intent.getIntExtra("assessment_score", 0)
        val lineDuration = intent.getStringExtra("line_duration") ?: ""
        val stage = intent.getStringExtra("stage") ?: ""

        // Display scores
        tvPatientScore.text = patientScore.toString()
        tvTotalScore.text = totalScore.toString()

        // Arrow placement
        scaleBar.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                scaleBar.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val barWidth = scaleBar.width
                val scoreRatio = patientScore.toFloat() / totalScore.toFloat()
                val arrowX = barWidth * scoreRatio
                ivArrow.translationX = arrowX - ivArrow.width / 2
            }
        })

        // Recommendations
        val recommendations = StringBuilder()
        recommendations.append("• Ensure aseptic techniques are followed.\n")

        if (lineDuration.contains("Week 3")) {
            recommendations.append("• If line duration >3 weeks, recommend immediate removal or exchange.\n")
        }
        if (stage == "CKD") {
            recommendations.append("• Encourage planning for AV fistula.\n")
        }
        if (patientScore >= 15) {
            recommendations.append("• High risk detected: Consider hospital admission and close monitoring.\n")
        } else if (patientScore in 8..14) {
            recommendations.append("• Moderate risk: Regular follow-up and preventive measures required.\n")
        } else {
            recommendations.append("• Low risk: Continue routine monitoring.\n")
        }

        tvRecommendations.text = recommendations.toString()

        // Done → back to dashboard
        btnDone.setOnClickListener {
            val intent = Intent(this@ScoreActivity, DoctorDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}

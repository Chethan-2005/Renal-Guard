package com.example.renalguard

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class AboutPatientDietingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_dieting) // Reusing the same layout

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        val tvDietInfo: TextView = findViewById(R.id.tvDietInfo)

        // Set up toolbar navigation click
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Static diet info
        val dietText = """
            Dietary Plans Which Can Be Followed By Dialysis Patients:

            • Limit potassium & phosphorus (avoid bananas, oranges, dairy).
            • Increase protein intake (especially in CKD patients on dialysis).
            • Fluid management guidelines.
            • Sodium restriction for hypertensive patients.
        """.trimIndent()
        tvDietInfo.text = dietText
    }
}

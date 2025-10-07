package com.example.renalguard

import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.TextView

class AboutUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        val tvAboutUs: TextView = findViewById(R.id.tvAboutUs)
        val toolbar: Toolbar = findViewById(R.id.toolbarAboutUs)

        tvAboutUs.text = getString(R.string.about_us_text)

        // Set up toolbar navigation click
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Handle system back button
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        // Apply text justification for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tvAboutUs.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }
    }
}

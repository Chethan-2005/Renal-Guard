package com.example.renalguard

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorSchedulesActivity : AppCompatActivity() {

    private lateinit var recyclerSchedules: RecyclerView
    private lateinit var adapter: ScheduleAdapter
    private lateinit var progressBar: ProgressBar
    private var doctorEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_schedules)

        recyclerSchedules = findViewById(R.id.recyclerSchedules)
        recyclerSchedules.layoutManager = LinearLayoutManager(this)
        progressBar = findViewById(R.id.progressBar)

        doctorEmail = intent.getStringExtra("doctor_email") ?: ""
        if (doctorEmail.isEmpty()) {
            Toast.makeText(this, "Doctor email missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Toolbar setup
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        loadSchedules()
    }

    private fun loadSchedules() {
        progressBar.visibility = View.VISIBLE
        val request = mapOf("doctor_email" to doctorEmail)

        ApiClient.instance.create(ApiService::class.java)
            .getDoctorSchedules(request)
            .enqueue(object : Callback<DoctorSchedulesResponse> {
                override fun onResponse(
                    call: Call<DoctorSchedulesResponse>,
                    response: Response<DoctorSchedulesResponse>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful && response.body()?.success == true) {
                        val schedules = response.body()?.schedules ?: emptyList()
                        adapter = ScheduleAdapter(schedules)
                        recyclerSchedules.adapter = adapter
                    } else {
                        Toast.makeText(
                            this@DoctorSchedulesActivity,
                            response.body()?.message ?: "No schedules found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DoctorSchedulesResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@DoctorSchedulesActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}

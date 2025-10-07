package com.example.renalguard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookAppointmentActivity : AppCompatActivity() {
    private lateinit var recyclerSchedules: RecyclerView
    private lateinit var adapter: PatientScheduleAdapter

    private var doctorEmail: String = ""
    private var patientEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_appointment)

        // toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)

        // back arrow click -> go to patient dashboard
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, PatientDashboardActivity::class.java)
            intent.putExtra("patient_email", patientEmail)
            startActivity(intent)
            finish()
        }

        recyclerSchedules = findViewById(R.id.recyclerSchedules)
        recyclerSchedules.layoutManager = LinearLayoutManager(this)

        doctorEmail = intent.getStringExtra("doctor_email") ?: ""
        patientEmail = intent.getStringExtra("patient_email") ?: ""

        if (doctorEmail.isEmpty()) {
            Toast.makeText(this, "Doctor email missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadDoctorSchedules(doctorEmail)
    }

    private fun loadDoctorSchedules(email: String) {
        // ✅ FIX: send "doctor_email" instead of "email"
        val request = mapOf("doctor_email" to email)

        ApiClient.instance.create(ApiService::class.java)
            .getDoctorSchedules(request)
            .enqueue(object : Callback<DoctorSchedulesResponse> {
                override fun onResponse(
                    call: Call<DoctorSchedulesResponse>,
                    response: Response<DoctorSchedulesResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val schedules = response.body()?.schedules ?: emptyList()

                        adapter = PatientScheduleAdapter(schedules) { schedule ->
                            val intent = Intent(
                                this@BookAppointmentActivity,
                                AvailableSlotsActivity::class.java
                            )
                            val sid = try { schedule.schedule_id.toInt() } catch (e: Exception) { 0 }
                            intent.putExtra("schedule_id", sid)
                            intent.putExtra("patient_email", patientEmail)
                            startActivity(intent)
                        }

                        recyclerSchedules.adapter = adapter
                    } else {
                        Toast.makeText(
                            this@BookAppointmentActivity,
                            response.body()?.message ?: "No schedules found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DoctorSchedulesResponse>, t: Throwable) {
                    Toast.makeText(
                        this@BookAppointmentActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}

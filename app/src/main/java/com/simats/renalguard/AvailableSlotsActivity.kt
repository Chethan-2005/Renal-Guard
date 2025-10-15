package com.simats.renalguard

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

class AvailableSlotsActivity : AppCompatActivity() {

    private lateinit var recyclerSlots: RecyclerView
    private lateinit var adapter: SlotsAdapter

    private var scheduleId: Int = 0
    private var patientEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_available_slots)

        // Toolbar with back arrow
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        recyclerSlots = findViewById(R.id.recyclerSlots)
        recyclerSlots.layoutManager = LinearLayoutManager(this)

        scheduleId = intent.getIntExtra("schedule_id", 0)
        patientEmail = intent.getStringExtra("patient_email") ?: ""

        if (scheduleId == 0) {
            Toast.makeText(this, "Invalid schedule", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadSlots(scheduleId)
    }

    private fun loadSlots(scheduleId: Int) {
        val request = ScheduleIdRequest(scheduleId)

        ApiClient.instance.create(ApiService::class.java)
            .getAvailableSlots(request)
            .enqueue(object : Callback<AvailableSlotsResponse> {
                override fun onResponse(
                    call: Call<AvailableSlotsResponse>,
                    response: Response<AvailableSlotsResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val slots = response.body()?.slots ?: emptyList()
                        adapter = SlotsAdapter(slots) { slot ->
                            bookSlot(scheduleId, patientEmail, slot)
                        }
                        recyclerSlots.adapter = adapter
                    } else {
                        Toast.makeText(
                            this@AvailableSlotsActivity,
                            response.body()?.message ?: "No slots found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AvailableSlotsResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AvailableSlotsActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun bookSlot(scheduleId: Int, patientEmail: String, slot: String) {
        if (patientEmail.isEmpty()) {
            Toast.makeText(this, "Patient email missing", Toast.LENGTH_SHORT).show()
            return
        }

        val request = BookAppointmentRequest(scheduleId, patientEmail, slot)

        ApiClient.instance.create(ApiService::class.java)
            .bookAppointment(request)
            .enqueue(object : Callback<BasicApiResponse> {
                override fun onResponse(
                    call: Call<BasicApiResponse>,
                    response: Response<BasicApiResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        // Navigate to booking confirmation screen
                        val intent = Intent(
                            this@AvailableSlotsActivity,
                            BookingConfirmedActivity::class.java
                        )
                        intent.putExtra("patient_email", patientEmail)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@AvailableSlotsActivity,
                            response.body()?.message ?: "Booking failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<BasicApiResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AvailableSlotsActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}

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
import java.text.SimpleDateFormat
import java.util.*

class BookAppointmentActivity : AppCompatActivity() {

    private lateinit var recyclerSchedules: RecyclerView
    private lateinit var adapter: PatientScheduleAdapter
    private var doctorEmail: String = ""
    private var patientEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_appointment)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
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
        val request = mapOf("doctor_email" to email)

        ApiClient.instance.create(ApiService::class.java)
            .getDoctorSchedules(request)
            .enqueue(object : Callback<DoctorSchedulesResponse> {
                override fun onResponse(
                    call: Call<DoctorSchedulesResponse>,
                    response: Response<DoctorSchedulesResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val allSchedules = response.body()?.schedules ?: emptyList()
                        val filteredSchedules = filterUpcomingSchedules(allSchedules)

                        if (filteredSchedules.isNotEmpty()) {
                            adapter = PatientScheduleAdapter(filteredSchedules) { schedule ->
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
                                "No upcoming schedules available",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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

    private fun filterUpcomingSchedules(schedules: List<DoctorSchedule>): List<DoctorSchedule> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val now = Calendar.getInstance().time

        return schedules.filter { schedule ->
            try {
                val scheduleDate = dateFormat.parse(schedule.available_date)
                val startTime = timeFormat.parse(schedule.start_time)
                val endTime = timeFormat.parse(schedule.end_time)

                if (scheduleDate == null || startTime == null || endTime == null) return@filter false

                val calStart = Calendar.getInstance().apply {
                    time = scheduleDate
                    val parts = schedule.start_time.split(":")
                    set(Calendar.HOUR_OF_DAY, parts[0].toInt())
                    set(Calendar.MINUTE, parts[1].toInt())
                    set(Calendar.SECOND, 0)
                }

                val calEnd = Calendar.getInstance().apply {
                    time = scheduleDate
                    val parts = schedule.end_time.split(":")
                    set(Calendar.HOUR_OF_DAY, parts[0].toInt())
                    set(Calendar.MINUTE, parts[1].toInt())
                    set(Calendar.SECOND, 0)
                }

                val start = calStart.time
                val end = calEnd.time

                schedule.status = getScheduleStatus(now, start, end, scheduleDate) // attach label

                // show if ongoing or upcoming
                return@filter now.before(end)
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun getScheduleStatus(now: Date, start: Date, end: Date, date: Date): String {
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = df.format(Date())
        val schedDate = df.format(date)

        return when {
            now.after(start) && now.before(end) -> "Ongoing"
            schedDate == today && now.before(start) -> {
                val diffMillis = start.time - now.time
                val diffMinutes = diffMillis / (1000 * 60)
                val hours = diffMinutes / 60
                val minutes = diffMinutes % 60
                "Starts in ${hours}h ${minutes}m"
            }
            date.after(Date()) -> {
                val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
                val tmrStr = df.format(tomorrow.time)
                if (schedDate == tmrStr) "Tomorrow" else "Upcoming"
            }
            else -> "Past"
        }
    }
}

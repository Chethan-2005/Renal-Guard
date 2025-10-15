package com.simats.renalguard

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AddScheduleActivity : AppCompatActivity() {

    private lateinit var etDate: TextInputEditText
    private lateinit var etStartTime: TextInputEditText
    private lateinit var etEndTime: TextInputEditText
    private lateinit var btnSubmit: Button
    private var doctorEmail: String = ""

    private val slotDuration = 15 // constant 15 minutes
    private var selectedStartTimeMillis: Long = 0 // For end time validation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_schedule)

        doctorEmail = intent.getStringExtra("doctor_email") ?: ""

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // Views
        etDate = findViewById(R.id.etDate)
        etStartTime = findViewById(R.id.etStartTime)
        etEndTime = findViewById(R.id.etEndTime)
        btnSubmit = findViewById(R.id.btnSubmitSchedule)

        // Date picker (only today or future)
        etDate.setOnClickListener {
            val c = Calendar.getInstance()
            val dpd = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val dateString = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    etDate.setText(dateString)
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            )
            // Disable past dates
            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()
        }

        // Time pickers with 12-hour format
        etStartTime.setOnClickListener { pickStartTime(etStartTime) }
        etEndTime.setOnClickListener { pickEndTime(etEndTime) }

        // Submit
        btnSubmit.setOnClickListener { saveSchedule() }
    }

    private fun pickStartTime(field: TextInputEditText) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        TimePickerDialog(this, { _, h, m ->
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, h)
            cal.set(Calendar.MINUTE, m)
            selectedStartTimeMillis = cal.timeInMillis // save start time
            val formatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
            field.setText(formatted)
        }, hour, minute, false).show()
    }

    private fun pickEndTime(field: TextInputEditText) {
        if (selectedStartTimeMillis == 0L) {
            Toast.makeText(this, "Please select start time first", Toast.LENGTH_SHORT).show()
            return
        }

        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        TimePickerDialog(this, { _, h, m ->
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, h)
            cal.set(Calendar.MINUTE, m)
            // Validate end time > start time
            if (cal.timeInMillis <= selectedStartTimeMillis) {
                Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show()
                return@TimePickerDialog
            }
            val formatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
            field.setText(formatted)
        }, hour, minute, false).show()
    }

    private fun saveSchedule() {
        val date = etDate.text.toString().trim()
        val start = etStartTime.text.toString().trim()
        val end = etEndTime.text.toString().trim()

        if (date.isEmpty() || start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val sdf12 = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())

        try {
            val start24 = sdf24.format(sdf12.parse(start)!!)
            val end24 = sdf24.format(sdf12.parse(end)!!)

            val startCal = Calendar.getInstance().apply { time = sdf24.parse(start24)!! }
            val endCal = Calendar.getInstance().apply { time = sdf24.parse(end24)!! }

            val diffMillis = endCal.timeInMillis - startCal.timeInMillis
            val diffMinutes = diffMillis / (1000 * 60)

            if (diffMinutes <= 0) {
                Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show()
                return
            }

            val maxPatients = (diffMinutes / slotDuration).toInt()

            val service = ApiClient.instance.create(ApiService::class.java)
            val request = AddScheduleRequest(
                doctor_email = doctorEmail,
                available_date = date,
                start_time = start,
                end_time = end,
                slot_duration = slotDuration,
                max_patients = maxPatients
            )

            service.addSchedule(request).enqueue(object : Callback<BasicApiResponse> {
                override fun onResponse(
                    call: Call<BasicApiResponse>,
                    response: Response<BasicApiResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@AddScheduleActivity,
                            "Schedule added successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@AddScheduleActivity,
                            response.body()?.message ?: "Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<BasicApiResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddScheduleActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        } catch (e: Exception) {
            Toast.makeText(this, "Invalid time format", Toast.LENGTH_SHORT).show()
        }
    }
}

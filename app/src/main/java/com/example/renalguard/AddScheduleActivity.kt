package com.example.renalguard

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
import java.util.*

class AddScheduleActivity : AppCompatActivity() {

    private lateinit var etDate: TextInputEditText
    private lateinit var etStartTime: TextInputEditText
    private lateinit var etEndTime: TextInputEditText
    private lateinit var etSlotDuration: TextInputEditText
    private lateinit var etMaxPatients: TextInputEditText
    private lateinit var btnSubmit: Button
    private var doctorEmail: String = ""

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
        etSlotDuration = findViewById(R.id.etSlotDuration)
        etMaxPatients = findViewById(R.id.etMaxPatients)
        btnSubmit = findViewById(R.id.btnSubmitSchedule)

        // Date picker
        etDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                etDate.setText("$y-${m + 1}-$d")
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Time pickers
        etStartTime.setOnClickListener { pickTime(etStartTime) }
        etEndTime.setOnClickListener { pickTime(etEndTime) }

        // Submit
        btnSubmit.setOnClickListener { saveSchedule() }
    }

    private fun pickTime(field: TextInputEditText) {
        val c = Calendar.getInstance()
        TimePickerDialog(this, { _, h, m ->
            val time = String.format("%02d:%02d", h, m)
            field.setText(time)
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
    }

    private fun saveSchedule() {
        val date = etDate.text.toString().trim()
        val start = etStartTime.text.toString().trim()
        val end = etEndTime.text.toString().trim()
        val slot = etSlotDuration.text.toString().trim()
        val max = etMaxPatients.text.toString().trim()

        if (date.isEmpty() || start.isEmpty() || end.isEmpty() || slot.isEmpty() || max.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val service = ApiClient.instance.create(ApiService::class.java)
        val request = AddScheduleRequest(
            doctor_email = doctorEmail,
            available_date = date,
            start_time = start,
            end_time = end,
            slot_duration = slot.toInt(),
            max_patients = max.toInt()
        )

        service.addSchedule(request).enqueue(object : Callback<BasicApiResponse> {
            override fun onResponse(call: Call<BasicApiResponse>, response: Response<BasicApiResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@AddScheduleActivity, "Schedule saved!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddScheduleActivity, response.body()?.message ?: "Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BasicApiResponse>, t: Throwable) {
                Toast.makeText(this@AddScheduleActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

package com.simats.renalguard

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientAppointmentsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PatientAppointmentAdapter
    private var patientEmail: String = ""
    private var doctorEmail: String = ""  // ✅ doctor email passed from dashboard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_appointments)

        recyclerView = findViewById(R.id.recyclerAppointments)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // get emails from intent
        patientEmail = intent.getStringExtra("patient_email") ?: ""
        doctorEmail = intent.getStringExtra("doctor_email") ?: ""

        // toolbar setup
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.title = "My Appointments"
        toolbar.setNavigationOnClickListener { finish() }

        if (doctorEmail.isEmpty()) {
            Toast.makeText(this, "Doctor email missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadAppointments()
    }

    private fun loadAppointments() {
        val request = mapOf(
            "doctor_email" to doctorEmail,
            "patient_email" to patientEmail
        )
        ApiClient.instance.create(ApiService::class.java)
            .getDoctorAppointments(request)
            .enqueue(object : Callback<DoctorAppointmentsResponse> {
                override fun onResponse(
                    call: Call<DoctorAppointmentsResponse>,
                    response: Response<DoctorAppointmentsResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val appointments = response.body()?.appointments ?: emptyList()
                        adapter = PatientAppointmentAdapter(appointments, patientEmail) { appt, status ->
                            updateAppointment(appt.appointment_id, patientEmail, status)
                        }
                        recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(
                            this@PatientAppointmentsActivity,
                            response.body()?.message ?: "No appointments found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DoctorAppointmentsResponse>, t: Throwable) {
                    Toast.makeText(
                        this@PatientAppointmentsActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun updateAppointment(appointmentId: String, patientEmail: String, status: String) {
        val service = ApiClient.instance.create(ApiService::class.java)
        val request = UpdateAppointmentRequest(appointmentId, patientEmail, status)

        val call = if (status == "cancelled") {
            service.deleteAppointment(request) // ✅ delete if cancelled
        } else {
            service.updateAppointment(request)
        }

        call.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(
                call: Call<GeneralResponse>,
                response: Response<GeneralResponse>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(
                        this@PatientAppointmentsActivity,
                        response.body()?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    loadAppointments() // refresh after update
                } else {
                    Toast.makeText(
                        this@PatientAppointmentsActivity,
                        response.body()?.message ?: "Failed to update",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                Toast.makeText(
                    this@PatientAppointmentsActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}

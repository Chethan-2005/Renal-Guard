package com.simats.renalguard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Request Models
data class DoctorEmailRequest(val doctor_email: String)
data class DoctorEmailSearchRequest(val doctor_email: String, val query: String)

class DoctorDashboardActivity : AppCompatActivity() {

    private lateinit var recyclerPatients: RecyclerView
    private lateinit var adapter: PatientAdapter
    private lateinit var searchBar: EditText
    private lateinit var noResultText: TextView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var progressBar: ProgressBar
    private lateinit var doctorEmail: String

    private var fullPatientList = mutableListOf<PatientModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_dashboard)

        // Doctor email
        doctorEmail = intent.getStringExtra("doctor_email")?.trim() ?: ""
        Log.d("DOCTOR_EMAIL", "Received doctor email: '$doctorEmail'")

        // Toolbar buttons
        val btnCalendar = findViewById<ImageButton>(R.id.btnCalendar)
        val btnInfo = findViewById<ImageButton>(R.id.btnInfo)

        btnCalendar.setOnClickListener {
            val intent = Intent(this, DoctorAppointmentOptionsActivity::class.java)
            intent.putExtra("doctor_email", doctorEmail)
            startActivity(intent)
        }

        btnInfo.setOnClickListener {
            startActivity(Intent(this, AboutDietingActivity::class.java))
        }

        // Views
        recyclerPatients = findViewById(R.id.recyclerViewPatients)
        searchBar = findViewById(R.id.searchBar)
        noResultText = findViewById(R.id.textNoResults)
        progressBar = findViewById(R.id.progressBar)
        bottomNav = findViewById(R.id.bottomNavigation)
        bottomNav.itemIconTintList = null

        // RecyclerView setup
        recyclerPatients.layoutManager = LinearLayoutManager(this)
        adapter = PatientAdapter(
            mutableListOf(),
            onDelete = { patient -> confirmDelete(patient) },
            onView = { patient -> viewPatientDetails(patient) }
        )
        recyclerPatients.adapter = adapter

        if (doctorEmail.isEmpty()) {
            Toast.makeText(this, "Doctor email missing!", Toast.LENGTH_LONG).show()
        }

        // Search functionality
        searchBar.addTextChangedListener {
            val query = it.toString().trim()
            if (query.isEmpty()) {
                loadPatients()
            } else {
                searchPatients(query)
            }
        }

        // Bottom Navigation
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_add -> {
                    val intent = Intent(this, AddPatientActivity::class.java)
                    intent.putExtra("doctor_email", doctorEmail)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, DoctorProfileActivity::class.java)
                    intent.putExtra("doctor_email", doctorEmail)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (doctorEmail.isNotEmpty()) {
            searchBar.setText("")
            loadPatients()
        }
    }

    private fun loadPatients() {
        progressBar.visibility = View.VISIBLE
        val service = ApiClient.instance.create(ApiService::class.java)
        val request = DoctorEmailRequest(doctor_email = doctorEmail)

        service.getPatients(request).enqueue(object : Callback<List<PatientModel>> {
            override fun onResponse(
                call: Call<List<PatientModel>>,
                response: Response<List<PatientModel>>
            ) {
                progressBar.visibility = View.GONE
                fullPatientList.clear()

                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    fullPatientList.addAll(response.body()!!)
                    adapter.updateData(fullPatientList)
                } else {
                    fullPatientList.clear()
                    adapter.updateData(emptyList())
                }
                toggleNoResults()
            }

            override fun onFailure(call: Call<List<PatientModel>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@DoctorDashboardActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                fullPatientList.clear()
                adapter.updateData(fullPatientList)
                toggleNoResults()
            }
        })
    }

    private fun searchPatients(query: String) {
        progressBar.visibility = View.VISIBLE
        val service = ApiClient.instance.create(ApiService::class.java)
        val request = DoctorEmailSearchRequest(doctor_email = doctorEmail, query = query)

        service.searchPatients(request).enqueue(object : Callback<List<PatientModel>> {
            override fun onResponse(
                call: Call<List<PatientModel>>,
                response: Response<List<PatientModel>>
            ) {
                progressBar.visibility = View.GONE
                fullPatientList.clear()

                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    fullPatientList.addAll(response.body()!!)
                }

                adapter.updateData(fullPatientList)
                toggleNoResults(query)
            }

            override fun onFailure(call: Call<List<PatientModel>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@DoctorDashboardActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                fullPatientList.clear()
                adapter.updateData(fullPatientList)
                toggleNoResults(query)
            }
        })
    }

    private fun toggleNoResults(query: String? = null) {
        if (fullPatientList.isEmpty()) {
            noResultText.text = if (!query.isNullOrEmpty()) {
                "No patients match \"$query\""
            } else {
                "No patients found for this doctor."
            }
            noResultText.visibility = View.VISIBLE
            recyclerPatients.visibility = View.GONE
        } else {
            noResultText.visibility = View.GONE
            recyclerPatients.visibility = View.VISIBLE
        }
    }

    private fun confirmDelete(patient: PatientModel) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Delete Patient")
            .setMessage("Are you sure you want to delete ${patient.name}?")
            .setPositiveButton("Yes") { _, _ -> deletePatient(patient) }
            .setNegativeButton("No", null)
            .create()
        dialog.show()
    }

    private fun deletePatient(patient: PatientModel) {
        val service = ApiClient.instance.create(ApiService::class.java)
        val request: Map<String, String> = mapOf("patient_id" to patient.patientId)

        service.deletePatient(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@DoctorDashboardActivity, "Patient deleted", Toast.LENGTH_SHORT).show()
                    loadPatients()
                } else {
                    Toast.makeText(this@DoctorDashboardActivity, "Failed to delete (Code: ${response.code()})", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@DoctorDashboardActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun viewPatientDetails(patient: PatientModel) {
        val intent = Intent(this, PatientDataActivity::class.java)
        intent.putExtra("patient_id", patient.patientId)
        intent.putExtra("patient_name", patient.name)
        intent.putExtra("patient_email", patient.patientEmail)
        intent.putExtra("doctor_email", doctorEmail)
        startActivity(intent)
    }
}

package com.example.renalguard

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DoctorAdapter
    private lateinit var etSearch: EditText

    private var adminEmail: String = ""

    private var allDoctors: List<DoctorModel> = listOf() // Full list from API

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        adminEmail = intent.getStringExtra("admin_email") ?: ""
        recyclerView = findViewById(R.id.recyclerDoctors)
        etSearch = findViewById(R.id.etSearch)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Bottom Nav buttons
        val btnHome: ImageButton = findViewById(R.id.btnHome)
        val btnAdd: ImageButton = findViewById(R.id.btnAdd)
        val btnProfile: ImageButton = findViewById(R.id.btnProfile)

        btnHome.setOnClickListener {
            // Reload doctor list
            loadDoctors()
        }

        btnAdd.setOnClickListener {
            val intent = Intent(this, AddDoctorActivity::class.java)
            startActivity(intent)
        }

        btnProfile.setOnClickListener {
            val intent = Intent(this, AdminProfileActivity::class.java)
            intent.putExtra("admin_email", adminEmail)
            intent.putExtra("source", "home")
            startActivity(intent)
        }

        adapter = DoctorAdapter(listOf(),
            onViewClick = { doctor ->
                val doc = DocModel(
                    doctor_id = doctor.doctor_id,
                    name = doctor.name,
                    email = doctor.email,
                    phone = doctor.phone,
                    specialization = doctor.specialization,
                    education = doctor.education,
                    location = doctor.location
                )
                val intent = Intent(this, DocDetailsActivity::class.java)
                intent.putExtra("doctor", doc)
                startActivity(intent)
            },
            onDeleteClick = { doctor ->
                val dialog = DeleteConfirmationDialog(
                    this,
                    onYesClicked = { deleteDoctor(doctor.doctor_id) },
                    onNoClicked = {
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
                    }
                )
                dialog.show()
            }
        )
        recyclerView.adapter = adapter

        // Load doctors from API
        loadDoctors()

        // Search filter
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterDoctors(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterDoctors(query: String) {
        val filteredList = allDoctors.filter { doctor ->
            doctor.name.contains(query, ignoreCase = true) ||
                    doctor.doctor_id.contains(query, ignoreCase = true) ||
                    doctor.email.contains(query, ignoreCase = true)
        }
        adapter.updateList(filteredList)
    }

    private fun loadDoctors() {
        ApiClient.instance.create(ApiService::class.java)
            .getDoctors()
            .enqueue(object : Callback<GetDoctorsResponse> {
                override fun onResponse(
                    call: Call<GetDoctorsResponse>,
                    response: Response<GetDoctorsResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        allDoctors = response.body()?.doctors ?: emptyList()
                        adapter.updateList(allDoctors)
                    } else {
                        Toast.makeText(
                            this@AdminDashboardActivity,
                            "Failed to load doctors",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GetDoctorsResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AdminDashboardActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun deleteDoctor(doctorId: String) {
        val body = mapOf("doctor_id" to doctorId)
        ApiClient.instance.create(ApiService::class.java)
            .deleteDoctor(body)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@AdminDashboardActivity,
                            "Doctor deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadDoctors()
                    } else {
                        Toast.makeText(
                            this@AdminDashboardActivity,
                            "Failed to delete",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AdminDashboardActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}

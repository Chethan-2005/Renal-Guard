package com.simats.renalguard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PatientAdapter(
    private var patients: MutableList<PatientModel>,
    private val onDelete: (PatientModel) -> Unit,
    private val onView: (PatientModel) -> Unit
) : RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    inner class PatientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvAge: TextView = view.findViewById(R.id.tvAge)
        val tvGender: TextView = view.findViewById(R.id.tvGender)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)  // ✅ added
        val tvPatientId: TextView = view.findViewById(R.id.tvPatientId)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
        val btnView: Button = view.findViewById(R.id.btnView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patients[position]

        holder.tvName.text = "Name: ${patient.name}"
        holder.tvAge.text = "Age: ${patient.age}"
        holder.tvGender.text = "Gender: ${patient.gender}"
        holder.tvEmail.text = "Email: ${patient.patientEmail}"   // ✅ bind email
        holder.tvPatientId.text = "ID: ${patient.patientId}"

        holder.btnDelete.setOnClickListener { onDelete(patient) }
        holder.btnView.setOnClickListener { onView(patient) }

        Log.d("ADAPTER", "Binding ${patient.name} (${patient.patientId}) - ${patient.patientEmail}")
    }

    override fun getItemCount(): Int = patients.size

    fun updateData(newPatients: List<PatientModel>) {
        patients.clear()
        patients.addAll(newPatients)
        notifyDataSetChanged()
        Log.d("ADAPTER", "Updated with ${patients.size} patients")
    }
}

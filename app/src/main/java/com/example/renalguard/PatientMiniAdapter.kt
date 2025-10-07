package com.example.renalguard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PatientMiniAdapter(
    private val patients: List<PatientMiniModel>
) : RecyclerView.Adapter<PatientMiniAdapter.PatientViewHolder>() {

    inner class PatientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPatientName: TextView = view.findViewById(R.id.tvPatientName)
        val tvSlotTime: TextView = view.findViewById(R.id.tvSlotTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient_mini, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patients[position]
        holder.tvPatientName.text = patient.patient_name
        holder.tvSlotTime.text = patient.slot_time
    }

    override fun getItemCount(): Int = patients.size
}

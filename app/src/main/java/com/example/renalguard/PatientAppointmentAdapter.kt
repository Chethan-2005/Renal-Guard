package com.example.renalguard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PatientAppointmentAdapter(
    private val appointments: List<AppointmentModel>,
    private val patientEmail: String,
    private val onStatusChange: (AppointmentModel, String) -> Unit
) : RecyclerView.Adapter<PatientAppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDoctorSlot: TextView = view.findViewById(R.id.tvDoctorSlot)
        val tvDateTime: TextView = view.findViewById(R.id.tvDateTime)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnAttended: Button = view.findViewById(R.id.btnAttended)
        val btnMissed: Button = view.findViewById(R.id.btnMissed)
        val btnCancel: Button = view.findViewById(R.id.btnCancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appt = appointments[position]

        holder.tvDoctorSlot.text = "Doctor Slot: ${appt.schedule_start} - ${appt.schedule_end}"
        holder.tvDateTime.text = "Date: ${appt.date} | Slot: ${appt.slot_time}"
        holder.tvStatus.text = "Status: ${appt.appointment_status}"

        val finalized = appt.appointment_status in listOf("attended", "missed", "cancelled")

        holder.btnAttended.isEnabled = !finalized
        holder.btnMissed.isEnabled = !finalized
        holder.btnCancel.isEnabled = !finalized

        holder.btnAttended.setOnClickListener { onStatusChange(appt, "attended") }
        holder.btnMissed.setOnClickListener { onStatusChange(appt, "missed") }
        holder.btnCancel.setOnClickListener { onStatusChange(appt, "cancelled") }
    }

    override fun getItemCount(): Int = appointments.size
}

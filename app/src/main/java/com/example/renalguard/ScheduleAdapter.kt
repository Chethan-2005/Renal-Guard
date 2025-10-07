package com.example.renalguard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ScheduleAdapter(
    private val schedules: List<DoctorSchedule>
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    inner class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvRemaining: TextView = view.findViewById(R.id.tvRemaining)
        val recyclerPatients: RecyclerView = view.findViewById(R.id.recyclerPatients)
        val patientContainer: LinearLayout = view.findViewById(R.id.patientContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = schedules[position]

        holder.tvDate.text = "Date: ${schedule.available_date}"
        holder.tvTime.text = "Time: ${schedule.start_time} - ${schedule.end_time}"
        holder.tvRemaining.text = "Remaining: ${schedule.remaining}"

        // Setup inner RecyclerView for patients
        if (schedule.patients.isNotEmpty()) {
            holder.patientContainer.visibility = View.VISIBLE
            holder.recyclerPatients.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.recyclerPatients.adapter = PatientMiniAdapter(schedule.patients)
        } else {
            holder.patientContainer.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = schedules.size
}

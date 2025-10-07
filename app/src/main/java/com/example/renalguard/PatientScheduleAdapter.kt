package com.example.renalguard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PatientScheduleAdapter(
    private var schedules: List<DoctorSchedule>,
    private val onBookClick: (DoctorSchedule) -> Unit
) : RecyclerView.Adapter<PatientScheduleAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvRemaining: TextView = itemView.findViewById(R.id.tvRemaining)
        val btnBook: Button = itemView.findViewById(R.id.btnBook)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = schedules[position]
        holder.tvDate.text = "Date: ${schedule.available_date}"
        holder.tvTime.text = "Time: ${schedule.start_time} - ${schedule.end_time}"
        holder.tvRemaining.text = "Remaining Slots: ${schedule.remaining}"

        holder.btnBook.setOnClickListener { onBookClick(schedule) }
    }

    override fun getItemCount(): Int = schedules.size

    fun updateData(newSchedules: List<DoctorSchedule>) {
        schedules = newSchedules
        notifyDataSetChanged()
    }
}

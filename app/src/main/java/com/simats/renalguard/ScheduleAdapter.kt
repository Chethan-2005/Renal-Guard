package com.simats.renalguard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.renalguard.R
import com.simats.renalguard.DoctorSchedule
import com.simats.renalguard.PatientMiniModel

class ScheduleAdapter(
    private val schedules: List<DoctorSchedule>
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    inner class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvRemaining: TextView = view.findViewById(R.id.tvRemaining)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
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

        // -------------------------------
        // Status & background logic
        // -------------------------------
        try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val startDateTime = sdf.parse("${schedule.available_date} ${schedule.start_time}")
            val endDateTime = sdf.parse("${schedule.available_date} ${schedule.end_time}")
            val now = java.util.Date()

            when {
                now.after(endDateTime) -> {
                    // Finished
                    holder.tvStatus.text = "Finished"
                    holder.itemView.setBackgroundColor(holder.itemView.context.getColor(android.R.color.darker_gray))
                    holder.tvStatus.setTextColor(holder.itemView.context.getColor(android.R.color.white))
                }
                now.before(startDateTime) -> {
                    // Upcoming
                    val diffMillis = startDateTime.time - now.time
                    val days = diffMillis / (1000 * 60 * 60 * 24)
                    val hours = (diffMillis / (1000 * 60 * 60) % 24)
                    holder.tvStatus.text = "${days} day${if (days != 1L) "s" else ""} to go"
                    holder.itemView.setBackgroundColor(holder.itemView.context.getColor(android.R.color.white))
                    holder.tvStatus.setTextColor(holder.itemView.context.getColor(android.R.color.black))
                }
                now.after(startDateTime) && now.before(endDateTime) -> {
                    // Ongoing
                    holder.tvStatus.text = "Ongoing"
                    holder.itemView.setBackgroundColor(holder.itemView.context.getColor(android.R.color.white))
                    holder.tvStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_blue_dark))
                }
                else -> {
                    holder.tvStatus.text = ""
                    holder.itemView.setBackgroundColor(holder.itemView.context.getColor(android.R.color.white))
                    holder.tvStatus.setTextColor(holder.itemView.context.getColor(android.R.color.black))
                }
            }
        } catch (e: Exception) {
            holder.tvStatus.text = ""
            holder.itemView.setBackgroundColor(holder.itemView.context.getColor(android.R.color.white))
            holder.tvStatus.setTextColor(holder.itemView.context.getColor(android.R.color.black))
        }

        // -------------------------------
        // Patients RecyclerView
        // -------------------------------
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

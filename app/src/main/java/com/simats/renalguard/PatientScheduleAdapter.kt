package com.simats.renalguard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class PatientScheduleAdapter(
    private var schedules: List<DoctorSchedule>,
    private val onBookClick: (DoctorSchedule) -> Unit
) : RecyclerView.Adapter<PatientScheduleAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvRemaining: TextView = itemView.findViewById(R.id.tvRemaining)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnBook: Button = itemView.findViewById(R.id.btnBook)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = schedules[position]

        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val startFormatted = try {
            outputFormat.format(inputFormat.parse(schedule.start_time)!!)
        } catch (e: Exception) { schedule.start_time }

        val endFormatted = try {
            outputFormat.format(inputFormat.parse(schedule.end_time)!!)
        } catch (e: Exception) { schedule.end_time }

        holder.tvDate.text = "Date: ${schedule.available_date}"
        holder.tvTime.text = "Time: $startFormatted - $endFormatted"
        holder.tvRemaining.text = "Remaining Slots: ${schedule.remaining}"
        holder.tvStatus.text = schedule.status ?: ""

        // colored status
        when {
            schedule.status?.contains("Ongoing") == true -> {
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_orange_dark))
            }
            schedule.status?.contains("Starts") == true -> {
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_green_dark))
            }
            schedule.status?.contains("Tomorrow") == true -> {
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_blue_dark))
            }
            else -> {
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.darker_gray))
            }
        }

        holder.btnBook.setOnClickListener { onBookClick(schedule) }
    }

    override fun getItemCount(): Int = schedules.size
}

package com.simats.renalguard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class SlotsAdapter(
    private val slots: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<SlotsAdapter.SlotViewHolder>() {

    inner class SlotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtSlotTime: TextView = view.findViewById(R.id.txtSlotTime)
        val btnBookSlot: Button = view.findViewById(R.id.btnBookSlot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.slot_item, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slots[position]

        // Convert 24-hour DB time to 12-hour AM/PM format
        val formattedSlot = try {
            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = inputFormat.parse(slot)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            // Fallback if format mismatched (e.g., "HH:mm")
            try {
                val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val date = inputFormat.parse(slot)
                outputFormat.format(date!!)
            } catch (ex: Exception) {
                slot // If still fails, show original
            }
        }

        holder.txtSlotTime.text = formattedSlot
        holder.btnBookSlot.setOnClickListener { onClick(slot) }
    }

    override fun getItemCount(): Int = slots.size
}

package com.example.renalguard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
        holder.txtSlotTime.text = slot
        holder.btnBookSlot.setOnClickListener { onClick(slot) }
    }

    override fun getItemCount(): Int = slots.size
}

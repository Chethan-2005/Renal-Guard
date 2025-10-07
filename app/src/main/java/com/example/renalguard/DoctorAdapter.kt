package com.example.renalguard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DoctorAdapter(
    private var doctorList: List<DoctorModel>,
    private val onViewClick: (DoctorModel) -> Unit,
    private val onDeleteClick: (DoctorModel) -> Unit
) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idText: TextView = itemView.findViewById(R.id.tvDoctorId)
        val nameText: TextView = itemView.findViewById(R.id.tvName)
        val specializationText: TextView = itemView.findViewById(R.id.tvSpecialization)
        val emailText: TextView = itemView.findViewById(R.id.tvEmail)
        val btnView: Button = itemView.findViewById(R.id.btnView)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctorList[position]

        holder.idText.text = "ID: ${doctor.doctor_id}"
        holder.nameText.text = "Name: ${doctor.name}"
        holder.specializationText.text = "Specialization: ${doctor.specialization}"
        holder.emailText.text = "Email: ${doctor.email}"

        holder.btnView.setOnClickListener { onViewClick(doctor) }
        holder.btnDelete.setOnClickListener { onDeleteClick(doctor) }
    }

    override fun getItemCount() = doctorList.size

    fun updateList(newList: List<DoctorModel>) {
        doctorList = newList
        notifyDataSetChanged()
    }
}

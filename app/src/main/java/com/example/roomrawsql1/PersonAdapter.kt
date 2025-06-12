package com.example.roomrawsql1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PersonAdapter(private var personsWithDepartment: List<PersonWithDepartment>) : RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewName)
        private val ageTextView: TextView = itemView.findViewById(R.id.textViewAge)
        private val departmentTextView: TextView = itemView.findViewById(R.id.textViewDepartment)

        fun bind(personWithDepartment: PersonWithDepartment) {
            nameTextView.text = personWithDepartment.person.name
            ageTextView.text = personWithDepartment.person.age.toString()
            departmentTextView.text = personWithDepartment.department?.departmentName ?: "Unknown"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(personsWithDepartment[position])
    }

    override fun getItemCount(): Int = personsWithDepartment.size

    fun updateData(newPersonsWithDepartment: List<PersonWithDepartment>) {
        personsWithDepartment = newPersonsWithDepartment
        notifyDataSetChanged()
    }
}


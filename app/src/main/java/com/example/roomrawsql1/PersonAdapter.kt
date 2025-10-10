package com.example.roomrawsql1

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PersonAdapter(private var personsWithDepartment: List<PersonWithDepartment>) : RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

    private var totalBindTime = 0L
    private var bindCount = 0

    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewName)
        private val ageTextView: TextView = itemView.findViewById(R.id.textViewAge)
        private val departmentTextView: TextView = itemView.findViewById(R.id.textViewDepartment)
        private val salaryTextView: TextView = itemView.findViewById(R.id.textViewSalary) // Add salary text view

        fun bind(personWithDepartment: PersonWithDepartment) {
            nameTextView.text = personWithDepartment.person.name
            ageTextView.text = personWithDepartment.person.age.toString()
            departmentTextView.text = personWithDepartment.department?.departmentName ?: "Unknown"
            salaryTextView.text = personWithDepartment.departmentSalary.toString() // Display salary
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val start = System.nanoTime()
        holder.bind(personsWithDepartment[position])
        val end = System.nanoTime()

        totalBindTime += (end - start)
        bindCount++
        Log.d("Item", "Item count: ${position + 1}")
        if (position == personsWithDepartment.size - 1) {
            val avg = totalBindTime / bindCount / 1_000_000.0
            Log.d("BindTiming", "Rata-rata waktu onBindViewHolder: %.2f ms".format(avg))

            // Reset supaya tidak terakumulasi terus
            totalBindTime = 0L
            bindCount = 0
        }
    }

    override fun getItemCount(): Int = personsWithDepartment.size

    fun updateData(newPersonsWithDepartment: List<PersonWithDepartment>) {
        personsWithDepartment = newPersonsWithDepartment
        notifyDataSetChanged()
    }
}


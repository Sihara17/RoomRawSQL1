package com.example.roomrawsql1

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.lifecycle.Observer
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var personViewModel: PersonViewModel
    private lateinit var departmentViewModel: DepartmentViewModel
    private lateinit var spinnerDepartment: Spinner
    private lateinit var personAdapter: PersonAdapter
    private lateinit var spinnerSortName: Spinner
    private lateinit var spinnerSortAge: Spinner
    private lateinit var spinnerSortDepartment: Spinner
    private lateinit var buttonSortOK: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi ViewModel
        personViewModel = ViewModelProvider(this).get(PersonViewModel::class.java)
        departmentViewModel = ViewModelProvider(this).get(DepartmentViewModel::class.java)

        // Inisialisasi RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPersons)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inisialisasi adapter
        personAdapter = PersonAdapter(emptyList())
        recyclerView.adapter = personAdapter

        // Inisialisasi Spinner
        // Initialize the spinner
        spinnerSortName = findViewById(R.id.spinnerSortName)
        spinnerSortAge = findViewById(R.id.spinnerSortAge)
        spinnerSortDepartment = findViewById(R.id.spinnerSortDepartment)
        buttonSortOK = findViewById(R.id.buttonSortOK)

        // Set up adapter for spinners
        val sortOptions = resources.getStringArray(R.array.sort_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerSortName.adapter = adapter
        spinnerSortAge.adapter = adapter
        spinnerSortDepartment.adapter = adapter

        personViewModel = ViewModelProvider(this).get(PersonViewModel::class.java)

        // Set the click listener for the "OK" button
        buttonSortOK.setOnClickListener {
            val nameSortOrder = if (spinnerSortName.selectedItemPosition == 0) "ASC" else "DESC"
            val ageSortOrder = if (spinnerSortAge.selectedItemPosition == 0) "ASC" else "DESC"
            val departmentSortOrder = if (spinnerSortDepartment.selectedItemPosition == 0) "ASC" else "DESC"

            // Apply combined sorting based on the selected sort order
            personViewModel.getPersonsSortedCombined(nameSortOrder, ageSortOrder, departmentSortOrder)
                .observe(this@MainActivity, Observer { sortedPersons ->
                    sortedPersons?.let { personAdapter.updateData(it) }
                })
        }


        // Insert dummy data hanya jika belum ada data
        lifecycleScope.launch {
            if (departmentViewModel.getAllDepartments().isEmpty()) {
                insertDummyDepartments()
            }
            if (personViewModel.allPersonsWithDepartment.value.isNullOrEmpty()) {
                insertDummyPersons()
            }
        }

        // Observasi data persons dengan department untuk update UI
        personViewModel.allPersonsWithDepartment.observe(this, { personsWithDepartment ->
            personAdapter.updateData(personsWithDepartment) // Update data di RecyclerView
        })

    }

    private suspend fun insertDummyDepartments() {
        // Data dummy untuk Department
        val departments = listOf(
            "Human Resources",
            "Information Technology",
            "Finance",
            "Marketing",
            "Sales",
            "Customer Support",
            "Product Management",
            "Engineering",
            "Operations",
            "Legal"
        )

        for (departmentName in departments) {
            departmentViewModel.insert(Department(departmentName = departmentName))
        }

    }

    private suspend fun insertDummyPersons() {
        // Data dummy untuk Person
        val departments = departmentViewModel.getAllDepartments()
        val names = listOf(
            "John Doe", "Jane Smith", "Alice Brown", "Bob Johnson",
            "Charlie Davis", "David Miller", "Eve Wilson", "Frank Harris",
            "Grace Clark", "Henry Lee"
        )
        val ages = listOf(25, 30, 35, 40, 22, 27, 32, 28, 29, 33)

        for (i in 0 until 10) {
            val name = names[i]
            val age = ages[i]
            val department = departments.random() // Pilih department secara acak
            personViewModel.insert(Person(name = name, age = age, departmentId = department.id))
        }
    }
}

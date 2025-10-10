package com.example.roomrawsql1

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.lifecycle.Observer
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var personViewModel: PersonViewModel
    private lateinit var departmentViewModel: DepartmentViewModel
    private lateinit var personAdapter: PersonAdapter
    private lateinit var spinnerSortName: Spinner
    private lateinit var spinnerSortAge: Spinner
    private lateinit var spinnerSortDepartment: Spinner
    private lateinit var spinnerSortSalary: Spinner
    private lateinit var buttonSortOK: Button
    private lateinit var buttonReset: Button

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
        spinnerSortSalary = findViewById(R.id.spinnerSortSalary)
        buttonSortOK = findViewById(R.id.buttonSortOK)
        buttonReset = findViewById(R.id.buttonReset)

        // Set up adapter for spinners
        val sortOptions = resources.getStringArray(R.array.sort_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerSortName.adapter = adapter
        spinnerSortAge.adapter = adapter
        spinnerSortDepartment.adapter = adapter
        spinnerSortSalary.adapter = adapter

        personViewModel = ViewModelProvider(this).get(PersonViewModel::class.java)

        // Set the click listener for the "OK" button
        buttonSortOK.setOnClickListener {
            val nameSortOrder = if (spinnerSortName.selectedItemPosition == 0) "ASC" else "DESC"
            val ageSortOrder = if (spinnerSortAge.selectedItemPosition == 0) "ASC" else "DESC"
            val departmentSortOrder =
                if (spinnerSortDepartment.selectedItemPosition == 0) "ASC" else "DESC"
            val salarySortOrder =
                if (spinnerSortSalary.selectedItemPosition == 0) "ASC" else "DESC"

            // Apply combined sorting based on the selected sort order
            personViewModel.getPersonsSortedCombined(
                nameSortOrder,
                ageSortOrder,
                departmentSortOrder,
                salarySortOrder
            )
                .observe(this@MainActivity, Observer { sortedPersons ->
                    sortedPersons?.let { personAdapter.updateData(it)
                        recyclerView.post {
                            recyclerView.smoothScrollToPosition(it.size - 1)
                        }}
                })
        }

        personViewModel.allPersonsWithDepartment.observe(this, { personsWithDepartment ->
            // If the list is empty, insert dummy data
            if (personsWithDepartment.isNullOrEmpty()) {
                lifecycleScope.launch {
                    if (departmentViewModel.getAllDepartments().isEmpty()) {
                        insertDummyDepartments()
                    }
                    if (personViewModel.allPersonsWithDepartment.value.isNullOrEmpty()) {
                        insertDummyPersons()
                    }
                }
            } else {
                // Update the RecyclerView with the data
                personAdapter.updateData(personsWithDepartment)
            }
        })

        // Ganti buttonReset.setOnClickListener dengan ini:
        buttonReset.setOnClickListener {
            lifecycleScope.launch {
                // Reset spinners ke default (ASC)
                spinnerSortName.setSelection(0)
                spinnerSortAge.setSelection(0)
                spinnerSortDepartment.setSelection(0)
                spinnerSortSalary.setSelection(0)

                // Load data urutan ID ASC
                personViewModel.getPersonsSortedById().observe(this@MainActivity) { personsWithDepartment ->
                    personAdapter.updateData(personsWithDepartment)
                    recyclerView.smoothScrollToPosition(0)
                }
            }
        }

    }

    private suspend fun insertDummyDepartments() {
        // Data untuk Department dengan salary
        departmentViewModel.insert(Department(id = 1, departmentName = "Customer Service", salary = 1000000))
        departmentViewModel.insert(Department(id = 2, departmentName = "Finance", salary = 1200000))
        departmentViewModel.insert(Department(id = 3, departmentName = "HR", salary = 1400000))
        departmentViewModel.insert(Department(id = 4, departmentName = "IT", salary = 1600000))
        departmentViewModel.insert(Department(id = 5, departmentName = "Legal", salary = 1500000))
        departmentViewModel.insert(Department(id = 6, departmentName = "Marketing", salary = 1350000))
        departmentViewModel.insert(Department(id = 7, departmentName = "Procurement", salary = 1300000))
        departmentViewModel.insert(Department(id = 8, departmentName = "Sales", salary = 1100000))
    }

    private suspend fun insertDummyPersons() {
        // Data untuk Person (id, name, age, departmentId)
        personViewModel.insert(Person(id = 1, name = "Andi Wijaya", age = 25, departmentId = 1))
        personViewModel.insert(Person(id = 2, name = "Budi Santoso", age = 30, departmentId = 4))
        personViewModel.insert(Person(id = 3, name = "Citra Sari", age = 35, departmentId = 5))
        personViewModel.insert(Person(id = 4, name = "Dedi Pratama", age = 40, departmentId = 7))
        personViewModel.insert(Person(id = 5, name = "Eka Putri", age = 22, departmentId = 8))
        personViewModel.insert(Person(id = 6, name = "Farhan Rahman", age = 27, departmentId = 1))
        personViewModel.insert(Person(id = 7, name = "Gina Permata", age = 32, departmentId = 4))
        personViewModel.insert(Person(id = 8, name = "Hendra Setiawan", age = 28, departmentId = 5))
        personViewModel.insert(Person(id = 9, name = "Indah Nuraini", age = 29, departmentId = 7))
        personViewModel.insert(Person(id = 10, name = "Joko Prabowo", age = 33, departmentId = 8))
        personViewModel.insert(Person(id = 11, name = "Krisna Wijaya", age = 26, departmentId = 1))
        personViewModel.insert(Person(id = 12, name = "Lina Suriani", age = 34, departmentId = 4))
        personViewModel.insert(Person(id = 13, name = "Miko Dwianto", age = 31, departmentId = 5))
        personViewModel.insert(Person(id = 14, name = "Nina Kusuma", age = 38, departmentId = 7))
        personViewModel.insert(Person(id = 15, name = "Oscar Setiawan", age = 24, departmentId = 8))
        personViewModel.insert(Person(id = 16, name = "Putu Artha", age = 36, departmentId = 1))
        personViewModel.insert(Person(id = 17, name = "Qori Fahmi", age = 29, departmentId = 4))
        personViewModel.insert(Person(id = 18, name = "Rita Anggraini", age = 33, departmentId = 5))
        personViewModel.insert(Person(id = 19, name = "Satria Prabowo", age = 40, departmentId = 7))
        personViewModel.insert(Person(id = 20, name = "Taufik Hidayat", age = 25, departmentId = 8))
        personViewModel.insert(Person(id = 21, name = "Umi Kalsum", age = 27, departmentId = 1))
        personViewModel.insert(Person(id = 22, name = "Vina Maulani", age = 32, departmentId = 4))
        personViewModel.insert(Person(id = 23, name = "Wawan Supriyadi", age = 28, departmentId = 5))
        personViewModel.insert(Person(id = 24, name = "Xenia Alamsyah", age = 30, departmentId = 7))
        personViewModel.insert(Person(id = 25, name = "Yogi Pratama", age = 24, departmentId = 8))
        personViewModel.insert(Person(id = 26, name = "Zainab Asmarani", age = 26, departmentId = 1))
        personViewModel.insert(Person(id = 27, name = "Agus Santoso", age = 35, departmentId = 4))
        personViewModel.insert(Person(id = 28, name = "Bambang Haryanto", age = 32, departmentId = 5))
        personViewModel.insert(Person(id = 29, name = "Cahya Mahendra", age = 27, departmentId = 7))
        personViewModel.insert(Person(id = 30, name = "Dewi Sulastri", age = 29, departmentId = 8))
        personViewModel.insert(Person(id = 31, name = "Eko Purwanto", age = 33, departmentId = 1))
        personViewModel.insert(Person(id = 32, name = "Fanny Rosadi", age = 34, departmentId = 4))
        personViewModel.insert(Person(id = 33, name = "Guntur Nugroho", age = 28, departmentId = 5))
        personViewModel.insert(Person(id = 34, name = "Heru Gunawan", age = 31, departmentId = 7))
        personViewModel.insert(Person(id = 35, name = "Intan Permata", age = 26, departmentId = 8))
        personViewModel.insert(Person(id = 36, name = "Jamilah Fadillah", age = 36, departmentId = 1))
        personViewModel.insert(Person(id = 37, name = "Kiki Lestari", age = 29, departmentId = 4))
        personViewModel.insert(Person(id = 38, name = "Lukman Hakim", age = 27, departmentId = 5))
        personViewModel.insert(Person(id = 39, name = "Marlina Sari", age = 25, departmentId = 7))
        personViewModel.insert(Person(id = 40, name = "Nurul Hidayati", age = 38, departmentId = 8))
        personViewModel.insert(Person(id = 41, name = "Oktavia Yuliana", age = 33, departmentId = 1))
        personViewModel.insert(Person(id = 42, name = "Panggih Widodo", age = 28, departmentId = 4))
        personViewModel.insert(Person(id = 43, name = "Rudi Saputra", age = 31, departmentId = 5))
        personViewModel.insert(Person(id = 44, name = "Siska Rizki", age = 30, departmentId = 7))
        personViewModel.insert(Person(id = 45, name = "Tina Melani", age = 24, departmentId = 8))
        personViewModel.insert(Person(id = 46, name = "Ujang Kurniawan", age = 36, departmentId = 1))
        personViewModel.insert(Person(id = 47, name = "Vivi Mulyani", age = 25, departmentId = 4))
        personViewModel.insert(Person(id = 48, name = "Wira Nugraha", age = 32, departmentId = 5))
        personViewModel.insert(Person(id = 49, name = "Yulia Dewi", age = 33, departmentId = 7))
        personViewModel.insert(Person(id = 50, name = "Zulfiqri Andriansyah", age = 30, departmentId = 8))
        personViewModel.insert(Person(id = 51, name = "Aditya Rahardja", age = 27, departmentId = 3))
        personViewModel.insert(Person(id = 52, name = "Bina Suryani", age = 28, departmentId = 4))
        personViewModel.insert(Person(id = 53, name = "Candra Kirana", age = 33, departmentId = 2))
        personViewModel.insert(Person(id = 54, name = "Dita Ayu", age = 29, departmentId = 7))
        personViewModel.insert(Person(id = 55, name = "Edi Haryanto", age = 26, departmentId = 8))
        personViewModel.insert(Person(id = 56, name = "Fajar Permana", age = 35, departmentId = 3))
        personViewModel.insert(Person(id = 57, name = "Gina Kurnia", age = 32, departmentId = 4))
        personViewModel.insert(Person(id = 58, name = "Hendy Wijaya", age = 27, departmentId = 2))
        personViewModel.insert(Person(id = 59, name = "Ilham Taufik", age = 30, departmentId = 7))
        personViewModel.insert(Person(id = 60, name = "Jihan Dewi", age = 31, departmentId = 8))
        personViewModel.insert(Person(id = 61, name = "Kaisar Ramadhan", age = 25, departmentId = 3))
        personViewModel.insert(Person(id = 62, name = "Liza Safitri", age = 28, departmentId = 4))
        personViewModel.insert(Person(id = 63, name = "Mia Nuryanti", age = 24, departmentId = 2))
        personViewModel.insert(Person(id = 64, name = "Nova Maheswari", age = 33, departmentId = 7))
        personViewModel.insert(Person(id = 65, name = "Omar Sulaiman", age = 35, departmentId = 8))
        personViewModel.insert(Person(id = 66, name = "Putri Hasanah", age = 32, departmentId = 3))
        personViewModel.insert(Person(id = 67, name = "Rina Setyani", age = 30, departmentId = 4))
        personViewModel.insert(Person(id = 68, name = "Sally Ramadhan", age = 29, departmentId = 2))
        personViewModel.insert(Person(id = 69, name = "Titi Wulandari", age = 28, departmentId = 7))
        personViewModel.insert(Person(id = 70, name = "Ulfa Anwar", age = 26, departmentId = 8))
        personViewModel.insert(Person(id = 71, name = "Vera Setiawati", age = 30, departmentId = 3))
        personViewModel.insert(Person(id = 72, name = "Wahid Wira", age = 31, departmentId = 4))
        personViewModel.insert(Person(id = 73, name = "Yanto Prasetyo", age = 33, departmentId = 2))
        personViewModel.insert(Person(id = 74, name = "Zidan Maulana", age = 24, departmentId = 7))
        personViewModel.insert(Person(id = 75, name = "Arief Wibowo", age = 30, departmentId = 8))
        personViewModel.insert(Person(id = 76, name = "Budi Purwanto", age = 32, departmentId = 3))
        personViewModel.insert(Person(id = 77, name = "Chandra Yuliana", age = 28, departmentId = 4))
        personViewModel.insert(Person(id = 78, name = "Dian Suryani", age = 26, departmentId = 2))
        personViewModel.insert(Person(id = 79, name = "Eka Fitri", age = 31, departmentId = 6))
        personViewModel.insert(Person(id = 80, name = "Faisal Malik", age = 34, departmentId = 8))
        personViewModel.insert(Person(id = 81, name = "Gusde Pradana", age = 27, departmentId = 3))
        personViewModel.insert(Person(id = 82, name = "Hilda Sari", age = 33, departmentId = 4))
        personViewModel.insert(Person(id = 83, name = "Iswan Hermawan", age = 32, departmentId = 2))
        personViewModel.insert(Person(id = 84, name = "Juno Satria", age = 28, departmentId = 6))
        personViewModel.insert(Person(id = 85, name = "Kamsiah Mutiara", age = 26, departmentId = 8))
        personViewModel.insert(Person(id = 86, name = "Liza Kristina", age = 31, departmentId = 3))
        personViewModel.insert(Person(id = 87, name = "Murdani Siahaan", age = 30, departmentId = 4))
        personViewModel.insert(Person(id = 88, name = "Novie Hapsari", age = 32, departmentId = 2))
        personViewModel.insert(Person(id = 89, name = "Oki Kurniawan", age = 30, departmentId = 6))
        personViewModel.insert(Person(id = 90, name = "Puti Rahayu", age = 33, departmentId = 8))
        personViewModel.insert(Person(id = 91, name = "Qimmy Fadhila", age = 29, departmentId = 3))
        personViewModel.insert(Person(id = 92, name = "Rizki Santika", age = 28, departmentId = 4))
        personViewModel.insert(Person(id = 93, name = "Sinta Purnama", age = 26, departmentId = 2))
        personViewModel.insert(Person(id = 94, name = "Tania Gita", age = 27, departmentId = 6))
        personViewModel.insert(Person(id = 95, name = "Ulyana Darmawan", age = 30, departmentId = 8))
        personViewModel.insert(Person(id = 96, name = "Vivi Sukma", age = 34, departmentId = 3))
        personViewModel.insert(Person(id = 97, name = "Widyanti Salim", age = 32, departmentId = 4))
        personViewModel.insert(Person(id = 98, name = "Yuliana Pramesti", age = 30, departmentId = 2))
        personViewModel.insert(Person(id = 99, name = "Zulfan Ginanjar", age = 33, departmentId = 6))
        personViewModel.insert(Person(id = 100, name = "Amir Zaki", age = 28, departmentId = 3))
    }
}

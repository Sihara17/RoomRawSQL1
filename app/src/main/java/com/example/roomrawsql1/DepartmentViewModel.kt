package com.example.roomrawsql1

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DepartmentViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DepartmentRepository

    init {
        val departmentDao = AppDatabase.getDatabase(application).departmentDao()
        repository = DepartmentRepository(departmentDao)
    }

    suspend fun getAllDepartments(): List<Department> {
        return repository.getAllDepartments()
    }

    fun insert(department: Department) = viewModelScope.launch {
        repository.insert(department)
    }

    // Add additional functions for salary-related queries if needed
    suspend fun getDepartmentsWithMinSalary(minSalary: Double): List<Department> {
        return repository.getDepartmentsWithMinSalary(minSalary)
    }
}

package com.example.roomrawsql1

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// In PersonViewModel.kt
class PersonViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PersonRepository

    // Now fetching all persons with their departments from the repository
    val allPersonsWithDepartment: LiveData<List<PersonWithDepartment>>

    init {
        val personDao = AppDatabase.getDatabase(application).personDao()
        repository = PersonRepository(personDao)
        allPersonsWithDepartment = repository.allPersonsWithDepartment
    }

    fun insert(person: Person) = viewModelScope.launch {
        repository.insert(person)
    }

    // Get persons sorted by name, age, and department combined
    fun getPersonsSortedCombined(
        nameSortOrder: String,
        ageSortOrder: String,
        departmentSortOrder: String,
        salarySortOrder: String
    ): LiveData<List<PersonWithDepartment>> {
        return repository.getPersonsSortedCombined(nameSortOrder, ageSortOrder, departmentSortOrder, salarySortOrder)
    }

    fun getPersonsSortedById(): LiveData<List<PersonWithDepartment>> {
        return repository.getPersonsSortedById()
    }
}









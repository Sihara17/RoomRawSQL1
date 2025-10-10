package com.example.roomrawsql1

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery

class PersonRepository(private val personDao: PersonDao) {

    val allPersonsWithDepartment: LiveData<List<PersonWithDepartment>> = personDao.getAllPersonsWithDepartment()

    // Make sure this method is calling the DAO's method correctly
    fun getPersonsSortedCombined(
        nameSortOrder: String,
        ageSortOrder: String,
        departmentSortOrder: String,
        salarySortOrder: String
    ): LiveData<List<PersonWithDepartment>> {
        // Construct the SQL query string
        val queryString = """
            SELECT persons.*, departments.*, departments.salary
            FROM persons 
            LEFT JOIN departments ON persons.department_id = departments.id
            ORDER BY persons.name $nameSortOrder, 
                     persons.age $ageSortOrder, 
                     departments.department_name $departmentSortOrder, 
                     departments.salary $salarySortOrder
        """
        // Create a SimpleSQLiteQuery with the query string
        val query = SimpleSQLiteQuery(queryString)

        // Call the DAO method with the query
        return personDao.getPersonsSortedCombined(query)
    }

    suspend fun insert(person: Person) {
        personDao.insert(person)
    }

    // PersonRepository.kt
    fun getPersonsSortedById(): LiveData<List<PersonWithDepartment>> {
        return personDao.getPersonsSortedById()
    }
}


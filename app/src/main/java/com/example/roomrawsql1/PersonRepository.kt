package com.example.roomrawsql1

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery

class PersonRepository(private val personDao: PersonDao) {

    val allPersonsWithDepartment: LiveData<List<PersonWithDepartment>> = personDao.getAllPersonsWithDepartment()

    // Make sure this method is calling the DAO's method correctly
    fun getPersonsSortedCombined(
        nameSortOrder: String,
        ageSortOrder: String,
        departmentSortOrder: String
    ): LiveData<List<PersonWithDepartment>> {
        // Construct the SQL query string
        val queryString = """
            SELECT * FROM persons 
            ORDER BY name $nameSortOrder, 
                     age $ageSortOrder, 
                     department_id $departmentSortOrder
        """
        // Create a SimpleSQLiteQuery with the query string
        val query = SimpleSQLiteQuery(queryString)

        // Call the DAO method with the query
        return personDao.getPersonsSortedCombined(query)
    }

    suspend fun insert(person: Person) {
        personDao.insert(person)
    }
}


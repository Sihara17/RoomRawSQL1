package com.example.roomrawsql1

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface PersonDao {

    @Insert
    suspend fun insert(person: Person)

    // Ensure this method is properly defined in PersonDao
    @RawQuery(observedEntities = [PersonWithDepartment::class])
    fun getPersonsSortedCombined(query: SupportSQLiteQuery): LiveData<List<PersonWithDepartment>>

    // @Transaction is used for relation queries
    @Transaction
    @Query("SELECT * FROM persons")
    fun getAllPersonsWithDepartment(): LiveData<List<PersonWithDepartment>>
}

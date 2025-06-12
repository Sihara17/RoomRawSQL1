package com.example.roomrawsql1

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DepartmentDao {
    @Insert
    suspend fun insert(department: Department)

    @Query("SELECT * FROM departments")
    suspend fun getAllDepartments(): List<Department>
}
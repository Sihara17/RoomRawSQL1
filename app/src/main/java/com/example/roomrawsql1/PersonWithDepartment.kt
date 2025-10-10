package com.example.roomrawsql1

import androidx.room.Embedded
import androidx.room.Relation

data class PersonWithDepartment(
    @Embedded val person: Person, // Data dari Person
    @Relation(
        parentColumn = "department_id", // Menghubungkan department_id di Person dengan id di Department
        entityColumn = "id" // Menghubungkan id di Department dengan department_id di Person
    )
    val department: Department?
) {
    val departmentSalary: Int
        get() = department?.salary ?: 0 // Add salary to be accessed easily
}

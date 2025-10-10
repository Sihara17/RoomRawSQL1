package com.example.roomrawsql1


class DepartmentRepository(private val departmentDao: DepartmentDao) {
    suspend fun getAllDepartments(): List<Department> {
        return departmentDao.getAllDepartments()
    }

    suspend fun insert(department: Department) {
        departmentDao.insert(department)
    }

    // Add additional functions if necessary
    suspend fun getDepartmentsWithMinSalary(minSalary: Double): List<Department> {
        return departmentDao.getDepartmentsWithMinSalary(minSalary)
    }
}

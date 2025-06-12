package com.example.roomrawsql1

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "persons",
    foreignKeys = [
        ForeignKey(
            entity = Department::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("department_id"),
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["department_id"])] // Add this line to create an index
)
data class Person(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "age") val age: Int,
    @ColumnInfo(name = "department_id") val departmentId: Int? = null
)




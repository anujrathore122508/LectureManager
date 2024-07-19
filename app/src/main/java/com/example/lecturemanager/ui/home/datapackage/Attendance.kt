package com.example.lecturemanager.ui.home.datapackage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lectureId: Long,
    val date: Long,
    val status: String
)


package com.example.lecturemanager.ui.home.datapackage

data class AttendanceSummary(
    val lectureName: String,
    val totalLectures: Int,
    val totalPresent: Int,
    val totalAbsent: Int,
    val attendancePercentage: Int
)

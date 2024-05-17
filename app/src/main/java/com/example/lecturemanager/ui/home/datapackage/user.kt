package com.example.lecturemanager.ui.home.datapackage


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Lecture")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val lectureName: String,
    val lectureDate:String,
    val lectureDay:String,
    val lectureStartTime: String,
    val lectureEndTime:String,

)

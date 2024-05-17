package com.example.lecturemanager.ui.home.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

import com.example.lecturemanager.ui.home.datapackage.User


@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM Lecture")
    suspend fun getAllUsers(): List<User>

    @Delete
    suspend fun delete(user: User)
}


package com.example.lecturemanager.ui.home.database


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lecturemanager.ui.home.dao.UserDao
import com.example.lecturemanager.ui.home.datapackage.Attendance
import com.example.lecturemanager.ui.home.datapackage.User

@Database(entities = [User::class, Attendance::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

}

object DatabaseBuilder {

    private var INSTANCE: AppDatabase? = null
    fun getInstance(context: android.content.Context): AppDatabase {


        synchronized(this) {
            var instance = INSTANCE
            if (instance == null) {
                instance = Room.databaseBuilder(
                                context.applicationContext,
                                AppDatabase::class.java,
                                "app_database"
                            ).fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
            }
            return instance
        }
    }
}

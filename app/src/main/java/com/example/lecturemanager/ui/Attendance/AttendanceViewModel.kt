package com.example.lecturemanager.ui.Attendance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lecturemanager.ui.home.database.DatabaseBuilder
import com.example.lecturemanager.ui.home.datapackage.AttendanceSummary
import com.example.lecturemanager.ui.home.dao.UserDao
import com.example.lecturemanager.ui.home.datapackage.DetailedAttendance
import com.example.lecturemanager.ui.home.datapackage.User
import kotlinx.coroutines.launch

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DatabaseBuilder.getInstance(application)
    private val userDao: UserDao = db.userDao()

    private val _attendanceSummary = MutableLiveData<List<AttendanceSummary>>()
    val attendanceSummary: LiveData<List<AttendanceSummary>> get() = _attendanceSummary

    private val _detailedAttendance = MutableLiveData<List<DetailedAttendance>>()
    val detailedAttendance: LiveData<List<DetailedAttendance>> get() = _detailedAttendance

    init {
        // Observe changes in users and update the summary accordingly
        viewModelScope.launch {
            userDao.getAAllUsers().collect { users ->
                calculateAttendanceSummary(users)
            }
        }

    }

    private suspend fun calculateAttendanceSummary(users: List<User>) {
        val summaryList = users.map { user ->
            val presentCount = userDao.getPresentCount(user.id)
            val totalCount = userDao.getTotalCount(user.id)
            val absentCount = totalCount - presentCount
            val percentage = if (totalCount > 0) (presentCount * 100) / totalCount else 0
            AttendanceSummary(
                lectureId = user.id,
                lectureName = user.lectureName,
                totalLectures = totalCount,
                totalPresent = presentCount,
                totalAbsent = absentCount,
                attendancePercentage = percentage
            )
        }
        _attendanceSummary.postValue(summaryList)
    }
    fun refreshAttendanceSummary() {
        viewModelScope.launch {
            userDao.getAAllUsers().collect { users ->
                calculateAttendanceSummary(users)
            }
        }
    }


    fun getDetailedAttendanceForLecture(lectureId: Long) {
        viewModelScope.launch {
            val detailedAttendanceList = userDao.getDetailedAttendance(lectureId)
            _detailedAttendance.postValue(detailedAttendanceList)
        }
    }

}

package com.example.lecturemanager.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.lecturemanager.ui.home.dao.UserDao
import com.example.lecturemanager.ui.home.datapackage.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val userDao: UserDao) : ViewModel() {

    val lectures: LiveData<List<User>> = userDao.getAllUsers()

    fun getLecturesForDay(day: String): LiveData<List<User>> {
        return userDao.getLecturesByDay(day)
    }

    fun insertLecture(user: User): LiveData<Long> {
        val result = MutableLiveData<Long>()
        viewModelScope.launch {
            val id = userDao.insert(user)
            result.postValue(id)
        }
        return result
    }
    fun deleteLecture(user: User) {
        viewModelScope.launch {
            userDao.deleteLecture(user)
        }
    }
    fun updateLecture(lecture: User): LiveData<Boolean> {
        return liveData {
            val result = withContext(Dispatchers.IO) {
                userDao.update(lecture)
                true // or false based on the success of the update operation
            }
            emit(result)
        }
    }


}



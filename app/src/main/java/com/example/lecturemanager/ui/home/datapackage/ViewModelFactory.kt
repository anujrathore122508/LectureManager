package com.example.lecturemanager.ui.home.datapackage


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lecturemanager.ui.home.HomeViewModel
import com.example.lecturemanager.ui.home.dao.UserDao


class HomeViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


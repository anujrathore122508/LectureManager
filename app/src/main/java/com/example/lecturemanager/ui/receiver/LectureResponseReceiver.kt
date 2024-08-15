package com.example.lecturemanager.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.WorkManager
import com.example.lecturemanager.ui.home.database.DatabaseBuilder
import com.example.lecturemanager.ui.home.datapackage.Attendance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LectureResponseReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val lectureId = intent?.getLongExtra("lectureId", -1) ?: return
        val action = intent.action
        val date = System.currentTimeMillis() // Current time as date

        Log.d("LectureResponseReceiver", "Received action: $action for lectureId: $lectureId")

        when (action) {
            "ACTION_ACCEPT" -> {
                updateAttendance(context, lectureId, date, "present")
                cancelNotification(context, lectureId.toInt())
            }
            "ACTION_DECLINE" -> {
                updateAttendance(context, lectureId, date, "absent")
                cancelNotification(context, lectureId.toInt())
            }
        }

        // Broadcast an update action
        context?.let {
            val updateIntent = Intent("ACTION_UPDATE_ATTENDANCE")
            LocalBroadcastManager.getInstance(it).sendBroadcast(updateIntent)
        }

        // Ensure WorkManager entry is cancelled after handling the notification
        WorkManager.getInstance(context!!).cancelAllWorkByTag("LectureNotification_$lectureId")
    }

    private fun updateAttendance(context: Context?, lectureId: Long, date: Long, status: String) {
        context?.let {
            val database = DatabaseBuilder.getInstance(it)
            val userDao = database.userDao()

            CoroutineScope(Dispatchers.IO).launch {
                val attendance = Attendance(
                    id = 0,
                    lectureId = lectureId,
                    date = date,
                    status = status
                )
                userDao.insertAttendance(attendance)
                Log.d("LectureResponseReceiver", "Attendance updated: $status for lectureId: $lectureId on date: $date")
            }
        }
    }

    private fun cancelNotification(context: Context?, notificationId: Int) {
        context?.let {
            val notificationManager = NotificationManagerCompat.from(it)
            notificationManager.cancel(notificationId)
            Log.d("LectureResponseReceiver", "Notification cancelled for notificationId: $notificationId")
        }
    }
}
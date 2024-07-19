package com.example.lecturemanager.ui.workers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit


object NotificationScheduler {
    fun scheduleLectureNotification(context: Context, lectureId: Long, lecture: String, startTimeMillis: Long) {
        val currentTimeMillis = System.currentTimeMillis()
        val delay = startTimeMillis - currentTimeMillis - TimeUnit.MINUTES.toMillis(5)

        if (delay > 0) {
            val notificationWork = OneTimeWorkRequestBuilder<LectureNotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("lectureId" to lectureId, "startTimeMillis" to startTimeMillis))
                .build()

            WorkManager.getInstance(context).enqueue(notificationWork)
        }
    }

    fun scheduleWeeklyNotification(context: Context, lectureId: Long, lecture: String, startTimeMillis: Long) {
        val repeatInterval = TimeUnit.DAYS.toMillis(7)
        val currentTimeMillis = System.currentTimeMillis()
        val delay = startTimeMillis - currentTimeMillis - TimeUnit.MINUTES.toMillis(5)

        if (delay > 0) {
            val notificationWork = PeriodicWorkRequestBuilder<LectureNotificationWorker>(repeatInterval, TimeUnit.MILLISECONDS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("lectureId" to lectureId, "startTimeMillis" to startTimeMillis))
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "LectureNotification_$lectureId",
                ExistingPeriodicWorkPolicy.REPLACE,
                notificationWork
            )
        }
    }
}




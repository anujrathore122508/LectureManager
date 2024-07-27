package com.example.lecturemanager.ui.workers


import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.Calendar
import java.util.concurrent.TimeUnit


object NotificationScheduler {
    fun scheduleLectureNotification(context: Context, lectureId: Long, lecture: String, startTimeMillis: Long) {
        val currentTimeMillis = System.currentTimeMillis()
        val delay = calculateInitialDelay(startTimeMillis, currentTimeMillis)

        if (delay > 0) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)
                .build()

            val notificationWork = OneTimeWorkRequestBuilder<LectureNotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .setInputData(workDataOf("lectureId" to lectureId, "startTimeMillis" to startTimeMillis))
                .build()

            WorkManager.getInstance(context).enqueue(notificationWork)
        }
    }

    fun scheduleWeeklyNotification(context: Context, lectureId: Long, lecture: String, startTimeMillis: Long) {
        val repeatInterval = TimeUnit.DAYS.toMillis(7)
        val currentTimeMillis = System.currentTimeMillis()
        val delay = calculateInitialDelay(startTimeMillis, currentTimeMillis)

        if (delay > 0) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)
                .build()

            val notificationWork = PeriodicWorkRequestBuilder<LectureNotificationWorker>(repeatInterval, TimeUnit.MILLISECONDS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .setInputData(workDataOf("lectureId" to lectureId, "startTimeMillis" to startTimeMillis))
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "LectureNotification_$lectureId",
                ExistingPeriodicWorkPolicy.REPLACE,
                notificationWork
            )
        }
    }

    private fun calculateInitialDelay(startTimeMillis: Long, currentTimeMillis: Long): Long {
        var delay = startTimeMillis - currentTimeMillis - TimeUnit.MINUTES.toMillis(5)

        if (delay <= 0) {
            val nextOccurrenceMillis = calculateNextOccurrence(startTimeMillis, currentTimeMillis)
            delay = nextOccurrenceMillis - currentTimeMillis - TimeUnit.MINUTES.toMillis(5)
        }

        return delay
    }

    private fun calculateNextOccurrence(startTimeMillis: Long, currentTimeMillis: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = startTimeMillis
        }
        val lectureDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val currentCalendar = Calendar.getInstance().apply {
            timeInMillis = currentTimeMillis
        }
        val currentDayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK)
        val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentCalendar.get(Calendar.MINUTE)
        val lectureHour = calendar.get(Calendar.HOUR_OF_DAY)
        val lectureMinute = calendar.get(Calendar.MINUTE)

        val daysUntilNextOccurrence = if (lectureDayOfWeek > currentDayOfWeek || (lectureDayOfWeek == currentDayOfWeek && (lectureHour > currentHour || (lectureHour == currentHour && lectureMinute > currentMinute)))) {
            lectureDayOfWeek - currentDayOfWeek
        } else {
            lectureDayOfWeek + 7 - currentDayOfWeek
        }

        currentCalendar.add(Calendar.DAY_OF_YEAR, daysUntilNextOccurrence)
        currentCalendar.set(Calendar.HOUR_OF_DAY, lectureHour)
        currentCalendar.set(Calendar.MINUTE, lectureMinute)
        currentCalendar.set(Calendar.SECOND, 0)

        return currentCalendar.timeInMillis
    }
}

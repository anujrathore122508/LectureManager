package com.example.lecturemanager.ui.workers

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.lecturemanager.R
import com.example.lecturemanager.ui.home.database.DatabaseBuilder
import com.example.lecturemanager.ui.receiver.LectureResponseReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

class LectureNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val lectureId = inputData.getLong("lectureId", -1)
        val startTimeMillis = inputData.getLong("startTimeMillis", 0L)


        if (lectureId == -1L || startTimeMillis == 0L) {
            Log.e("LectureNotificationWorker", "Invalid input data: lectureId = $lectureId, startTimeMillis = $startTimeMillis")
            return Result.failure()
        }

        // Check if the notification permission is granted
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("LectureNotificationWorker", "Notification permission not granted")
            return Result.failure()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val database = DatabaseBuilder.getInstance(applicationContext)
            val userDao = database.userDao()
            val user = userDao.getUserById(lectureId)
            user?.let {
                val acceptIntent = Intent(applicationContext, LectureResponseReceiver::class.java).apply {
                    action = "ACTION_ACCEPT"
                    putExtra("lectureId", lectureId)
                }
                val declineIntent = Intent(applicationContext, LectureResponseReceiver::class.java).apply {
                    action = "ACTION_DECLINE"
                    putExtra("lectureId", lectureId)
                }

                val acceptPendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    (lectureId + 1000).toInt(),  // Ensure unique request code
                    acceptIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val declinePendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    (lectureId + 2000).toInt(),  // Ensure unique request code
                    declineIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

//                val notification = NotificationCompat.Builder(applicationContext, "lecture_channel")
//                    .setSmallIcon(R.drawable.icon55)
////                    .setContentTitle("Lecture Reminder")
////                    .setContentText("Your lecture '${user.lectureName}' is about to start in 5 minutes.")
//                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .addAction(R.drawable.baseline_check_circle_24, "Accept", acceptPendingIntent)
//                    .addAction(R.drawable.baseline_cancel_24, "Decline", declinePendingIntent)
//                    .build()
//
//                with(NotificationManagerCompat.from(applicationContext)) {
//                    notify(lectureId.toInt(), notification)
//                }
                val notificationLayout = RemoteViews(applicationContext.packageName, R.layout.notification_custom)

// Set dynamic content if needed
                notificationLayout.setTextViewText(R.id.notification_title, "Lecture Reminder!")

                notificationLayout.setTextViewText(R.id.notification_text, "Your Lecture '${user.lectureName}' is about to start in 5 minutes.")

// Set intents for buttons
                notificationLayout.setOnClickPendingIntent(R.id.notification_accept, acceptPendingIntent)
                notificationLayout.setOnClickPendingIntent(R.id.notification_decline, declinePendingIntent)

                val notification = NotificationCompat.Builder(applicationContext, "lecture_channel")
                    .setSmallIcon(R.drawable.icon55)
                    .setCustomContentView(notificationLayout)
                    .setCustomBigContentView(notificationLayout)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build()

                with(NotificationManagerCompat.from(applicationContext)) {
                    notify(lectureId.toInt(), notification)
                }

                Log.d("LectureNotificationWorker", "Notification sent for lectureId: $lectureId")
            }
        }


        // Reschedule the notification for the next week only if the current time is before the start time
        if (System.currentTimeMillis() < startTimeMillis) {
            rescheduleWeeklyNotification(lectureId, startTimeMillis)
        }

        return Result.success()
    }
    private fun rescheduleWeeklyNotification(lectureId: Long, startTimeMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startTimeMillis
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val currentTimeMillis = System.currentTimeMillis()
        val nextNotificationTimeMillis = calculateNextNotificationTimeMillis(calendar, dayOfWeek, currentTimeMillis)

        val delay = nextNotificationTimeMillis - currentTimeMillis - TimeUnit.MINUTES.toMillis(5)

        if (delay > 0) {
            val nextNotificationWork = OneTimeWorkRequestBuilder<LectureNotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("LectureNotification_$lectureId")
                .setInputData(workDataOf("lectureId" to lectureId, "startTimeMillis" to startTimeMillis))
                .build()

            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                "LectureNotification_$lectureId",
                ExistingWorkPolicy.REPLACE,
                nextNotificationWork
            )
        } else {
            Log.e("LectureNotificationWorker", "Invalid delay for rescheduled notification: $delay")
        }
    }



    private fun calculateNextNotificationTimeMillis(calendar: Calendar, dayOfWeek: Int, currentTimeMillis: Long): Long {
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        var daysToAdd = (dayOfWeek - currentDayOfWeek).absoluteValue
        if (dayOfWeek <= currentDayOfWeek) {
            daysToAdd += 7
        }

        calendar.timeInMillis = currentTimeMillis + TimeUnit.DAYS.toMillis(daysToAdd.toLong())
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, 0)

        return calendar.timeInMillis
    }
}


package com.example.donemate.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.donemate.R
import com.example.donemate.data.common.Result
import com.example.donemate.data.repository.offline_first.TasksRepository
import com.example.donemate.receiver.ChangeTaskProgressStageReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.UUID
import kotlin.time.Clock

@Singleton
class NotificationHelper @Inject constructor(@ApplicationContext context: Context,
                                             private val tasksRepository: TasksRepository
) {

    private val appContext = context

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val reminderChannelName = "reminders"

    fun setUpNotificationChannel() {
        if (notificationManager.getNotificationChannel("reminder") == null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    reminderChannelName,
                    "Reminders",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "Reminders for tasks"
                },
            )
        }
    }

    suspend fun createMessageAndShow(taskId: String){
        val taskResult = tasksRepository.getTaskById(taskId)
        if (taskResult is Result.Success) {

            val id = UUID.randomUUID().hashCode().toString()
            val doneIntent = Intent(appContext, ChangeTaskProgressStageReceiver::class.java).apply {
                action = "done"
                putExtra("id", taskId)
                putExtra("action", "done")
            }


            val donePendingIntent: PendingIntent =
                PendingIntent.getBroadcast(
                    appContext, id.hashCode(), doneIntent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )

            val inProgressIntent =
                Intent(appContext, ChangeTaskProgressStageReceiver::class.java).apply {
                    action = "in_progress"
                    putExtra("id", taskId)
                    putExtra("action", "in_progress")
                }
            val inProgressPendingIntent: PendingIntent =
                PendingIntent.getBroadcast(
                    appContext, id.hashCode(), inProgressIntent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )


            val builder = NotificationCompat.Builder(appContext, reminderChannelName)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Reminder for ${taskResult.data.title}")
                .setContentText("Have you completed it?")
                .addAction(
                    R.drawable.done_icon,
                    "Done",
                    donePendingIntent
                )
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(
                    R.drawable.in_progress_icon,
                    "In Progress",
                    inProgressPendingIntent
                )
            if (ActivityCompat.checkSelfPermission(
                    appContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(id.toInt(), builder.build())
            }
        }
    }
}
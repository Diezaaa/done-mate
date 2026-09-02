package com.example.donemate.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.donemate.data.common.Result
import com.example.donemate.data.repository.offline_first.TasksRepository
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// TODO: The receiver doesn't restore alarms. Fix it.
@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var tasksRepository: TasksRepository



    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {


            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingResult = goAsync()
            var savedAlarms: List<Pair<String, Long>> = emptyList()
            CoroutineScope(Dispatchers.IO).launch {
                val tasks = tasksRepository.notArchivedTasks.first()
                if (tasks is Result.Success) {
                    savedAlarms = tasks.data
                        .filter { it.reminderDateTime != null }
                        .map { task ->
                            Pair(
                                first = task.id,
                                second = task.reminderDateTime!!.toEpochMilli()
                            )
                        }
                }


                val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    alarmManager.canScheduleExactAlarms()
                } else {
                    true
                }

                for (alarm in savedAlarms) {
                    val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                        putExtra("id", alarm.first)
                    }

                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        alarm.first.hashCode(),
                        alarmIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    if (canScheduleExact) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            alarm.second,
                            pendingIntent
                        )
                    }
                }
                Log.d("BootReceiver", "Alarms restored")
                pendingResult.finish()

            }
        }
    }
}
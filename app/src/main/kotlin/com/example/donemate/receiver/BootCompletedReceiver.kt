package com.example.donemate.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.donemate.alarm.AlarmScheduler
import com.example.donemate.data.common.Result
import com.example.donemate.data.repository.offline_first.TasksRepository
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var tasksRepository: TasksRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler


    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val result = tasksRepository.tasks.first()

                    if (result is Result.Success) {
                        result.data
                            .filter { it.reminderDateTime != null }
                            .forEach { task ->
                                alarmScheduler.setReminder(
                                    task.reminderDateTime?.toEpochMilliseconds(),
                                    task.id
                                )
                            }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
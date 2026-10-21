package com.example.donemate.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.donemate.data.common.Result
import com.example.donemate.data.model.Progress
import com.example.donemate.data.repository.offline_first.TasksRepository
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangeTaskProgressStageReceiver : BroadcastReceiver() {

    @Inject
    lateinit var tasksRepository: TasksRepository

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            val id = intent.getStringExtra("id")
            val action = intent.getStringExtra("action")

            if (id == null) {
                pendingResult.finish()
                return@launch
            }


            val task = tasksRepository.getTaskById(id)

            if (task is Result.Success) {
                if (action == "done") {
                    tasksRepository.upsert(task.data.copy(progress = Progress.COMPLETED.value))
                } else {
                    val newTask = task.data.copy(progress = Progress.IN_PROGRESS.value)
                    tasksRepository.upsert(newTask)
                }
                tasksRepository.sync()
            }

            pendingResult.finish()
        }
    }
}
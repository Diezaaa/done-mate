package com.example.donemate.domain

import com.example.donemate.alarm.AlarmScheduler
import com.example.donemate.data.common.Result
import com.example.donemate.data.image.TaskImagesLocalDataSource
import com.example.donemate.data.model.Task
import com.example.donemate.data.repository.offline_first.TasksRepository
import jakarta.inject.Inject

/**
 * A use case which deals with saving task information, its image and scheduling a reminder
 */
class SaveTaskUseCase @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val taskImagesLocalDataSource: TaskImagesLocalDataSource,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(task: Task): SaveTaskState {
        when (val result = tasksRepository.upsert(task)) {
            is Result.Error -> {
                return SaveTaskState.Error(result.message)
            }

            is Result.Success -> {
                task.reminderDateTime?.let { dateTime ->
                    alarmScheduler.setReminder(
                        dateTime.toEpochMilliseconds(),
                        task.id
                    )
                }
                val imageUri = task.imageUri?.let { imageUri ->
                    taskImagesLocalDataSource.write(taskImagesLocalDataSource.read(imageUri))
                }
                if (imageUri != null) tasksRepository.upsert(
                    task.copy(
                        imageUri = imageUri,
                    )
                )
                return SaveTaskState.Success
            }
        }
    }
}

sealed interface SaveTaskState {
    data class Error(val message: String) : SaveTaskState
    data object Success : SaveTaskState
}


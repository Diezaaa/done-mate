package com.example.donemate.domain

import com.example.donemate.alarm.AlarmScheduler
import com.example.donemate.data.common.Result
import com.example.donemate.data.image.ImageStorage
import com.example.donemate.data.model.Task
import com.example.donemate.data.repository.offline_first.TasksRepository
import jakarta.inject.Inject

/**
 * A use case which deals with saving task information, its image and scheduling a reminder
 */
class SaveTaskUseCase @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val imageStorage: ImageStorage,
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
                        dateTime.toEpochMilli(),
                        task.id
                    )
                }
                task.imageUri?.let { imageUri ->
                    imageStorage.write(imageStorage.read(imageUri))
                }
                return SaveTaskState.Success
            }
        }
    }
}

sealed interface SaveTaskState {
    data class Error(val message: String) : SaveTaskState
    data object Success : SaveTaskState
}


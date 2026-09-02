package com.example.donemate.ui.screens.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.alarm.AlarmScheduler
import com.example.donemate.data.common.Result
import com.example.donemate.data.image.ImageStorage
import com.example.donemate.data.model.Task
import com.example.donemate.data.repository.offline_first.TasksRepository
import com.example.donemate.domain.SaveTaskState
import com.example.donemate.domain.SaveTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val saveTaskUseCase: SaveTaskUseCase,
    private val tasksRepository: TasksRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<TaskFormUiState>(TaskFormUiState.Loaded(Task()))
    val uiState: StateFlow<TaskFormUiState> = _uiState.asStateFlow()
    private var hasLoaded = false

    fun loadTask(id: String) {
        if (!hasLoaded) {
            hasLoaded = true
            viewModelScope.launch {
                when (val result = tasksRepository.getTaskById(id)) {
                    is Result.Error -> {
                        _uiState.value = TaskFormUiState.Error(result.message)
                    }

                    is Result.Success -> {
                        _uiState.value = TaskFormUiState.Loaded(result.data)
                    }
                }
            }
        }
    }


    fun updateTitle(title: String) {
        _uiState.updateIfLoaded { loadedState ->
            loadedState.copy(task = loadedState.task.copy(title = title))
        }
    }

    fun updateProgress(progress: Int) {
        _uiState.updateIfLoaded { loadedState ->
            loadedState.copy(task = loadedState.task.copy(progress = progress))
        }
    }

    fun updateImageUri(imageUri: String) {
        _uiState.updateIfLoaded { loadedState ->
            loadedState.copy(task = loadedState.task.copy(imageUri = imageUri))
        }
    }

    fun updateDescription(description: String) {
        _uiState.updateIfLoaded { loadedState ->
            loadedState.copy(task = loadedState.task.copy(description = description))
        }
    }

    fun saveTask() {
        validateForm()
        val currentState = _uiState.value

        if (currentState is TaskFormUiState.Loaded) {
            _uiState.value = TaskFormUiState.Loading
            viewModelScope.launch {
                when (val result = saveTaskUseCase(currentState.task)) {
                    is SaveTaskState.Error -> {
                        _uiState.value = TaskFormUiState.Error(result.message)
                    }
                    is SaveTaskState.Success -> {
                        _uiState.value = TaskFormUiState.Saved
                    }
                }
            }
        }
    }


    fun validateForm() {
        _uiState.updateIfLoaded { loadedState ->
            if (loadedState.task.title.isEmpty()) {
                loadedState.copy(isValidForm = false)
            } else {
                loadedState.copy(isValidForm = true)
            }
        }
    }

    fun updateReminderDateTime(millis: Long?) {
        millis ?: return

        _uiState.updateIfLoaded { loadedState ->
            loadedState.copy(
                task = loadedState.task.copy(
                    reminderDateTime = Instant.ofEpochMilli(
                        millis
                    )
                )
            )
        }
    }

    fun updateDueDateTime(millis: Long?) {
        millis ?: return

        _uiState.updateIfLoaded { loadedState ->
            loadedState.copy(task = loadedState.task.copy(dueDateTime = Instant.ofEpochMilli(millis)))
        }
    }
}

sealed interface TaskFormUiState {
    data object Loading : TaskFormUiState
    data class Loaded(val task: Task, val isValidForm: Boolean = true) : TaskFormUiState
    data object Saved : TaskFormUiState
    data class Error(val message: String) : TaskFormUiState
}

fun MutableStateFlow<TaskFormUiState>.updateIfLoaded(
    transform: (TaskFormUiState.Loaded) -> TaskFormUiState.Loaded
) {
    update { currentState ->
        if (currentState is TaskFormUiState.Loaded) {
            transform(currentState)
        } else {
            currentState
        }
    }
}
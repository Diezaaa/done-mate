package com.example.donemate.ui.screens.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.common.Result
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
import javax.inject.Inject
import kotlin.time.Instant

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
        _uiState.updateIfLoaded {
            it.copy(task = it.task.copy(title = title))
        }
    }

    fun updateProgress(progress: Int) {
        _uiState.updateIfLoaded {
            it.copy(task = it.task.copy(progress = progress))
        }
    }

    fun updateImageUri(imageUri: String) {
        _uiState.updateIfLoaded {
            it.copy(task = it.task.copy(imageUri = imageUri))
        }
    }

    fun updateDescription(description: String) {
        _uiState.updateIfLoaded {
            it.copy(task = it.task.copy(description = description))
        }
    }

    fun saveTask() {
        _uiState.value.let {
            if (it is TaskFormUiState.Loaded) {
                _uiState.value = TaskFormUiState.Loading
                viewModelScope.launch {
                    when (val result = saveTaskUseCase(it.task)) {
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
    }

    fun updateReminderDateTime(millis: Long?) {
        millis ?: return

        _uiState.updateIfLoaded {
            it.copy(
                task = it.task.copy(
                    reminderDateTime = Instant.fromEpochMilliseconds(
                        millis
                    )
                )
            )
        }
    }

    fun updateDueDateTime(millis: Long?) {
        millis ?: return

        _uiState.updateIfLoaded {
            it.copy(
                task = it.task.copy(
                    dueDateTime = Instant.fromEpochMilliseconds(
                        millis
                    )
                )
            )
        }
    }
}

sealed interface TaskFormUiState {
    data object Loading : TaskFormUiState
    data class Loaded(val task: Task) : TaskFormUiState
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
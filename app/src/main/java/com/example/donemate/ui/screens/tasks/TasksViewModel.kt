package com.example.donemate.ui.screens.tasks


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.common.Result
import com.example.donemate.data.model.Task
import com.example.donemate.data.repository.offline_first.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: Find out a better way to handle tasks sorting and filtering
sealed interface TasksUiState {
    data object Loading : TasksUiState
    data class Success(
        private val tasks: List<Task>,
        val syncingStatus: Result<Unit>? = null,
        val query: String = "",
        val progressFilter: Int = -1,
        val sortingType: SortingType = SortingType.DueDateTime
    ) : TasksUiState {
        val visibleTasks: List<Task>
            get() = tasks
                .filter { task ->
                    query.isBlank() ||
                            task.title.contains(query, ignoreCase = true) ||
                            task.description?.contains(query, ignoreCase = true) == true
                }
                .let { searchedTasks ->
                    if (query.isNotBlank()) {
                        searchedTasks
                    } else {
                        searchedTasks.filter { task ->
                            progressFilter == -1 ||
                                    task.progress == progressFilter
                        }
                    }
                }
                .let { filteredTasks ->
                    when (sortingType) {
                        SortingType.TimeUpdated ->
                            filteredTasks.sortedByDescending { it.updateTime }

                        SortingType.ReminderDateTime ->
                            filteredTasks.sortedByDescending { it.reminderDateTime }

                        SortingType.DueDateTime ->
                            filteredTasks.sortedByDescending { it.dueDateTime }
                    }
                }
    }

    data class Error(val message: String) : TasksUiState
}

// TODO: Learn about inline functions
inline fun MutableStateFlow<TasksUiState>.updateIfSuccess(
    transform: (TasksUiState.Success) -> TasksUiState.Success
) {
    update { currentState ->
        if (currentState is TasksUiState.Success) {
            transform(currentState)
        } else {
            currentState
        }
    }
}

sealed interface SortingType {
    data object TimeUpdated : SortingType
    data object ReminderDateTime : SortingType
    data object DueDateTime : SortingType
}

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<TasksUiState>(
        TasksUiState.Loading
    ).also { state ->
        tasksRepository.tasks
            .onEach { result ->
                when (result) {
                    is Result.Success -> {
                        val current = state.value as? TasksUiState.Success

                        state.value = TasksUiState.Success(
                            tasks = result.data,
                            query = current?.query.orEmpty(),
                            progressFilter = current?.progressFilter ?: -1,
                            sortingType = current?.sortingType
                                ?: SortingType.DueDateTime
                        )
                    }

                    is Result.Error -> {
                        state.value = TasksUiState.Error(
                            message = result.message
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }
    val uiState = _uiState.asStateFlow()

    fun updateQuery(query: String) {
        _uiState.updateIfSuccess { currentState ->
            currentState.copy(query = query)
        }
    }

    fun updateSortingType(sortingType: SortingType) {
        _uiState.updateIfSuccess { currentState ->
            currentState.copy(sortingType = sortingType)
        }
    }

    fun updateProgressFilter(progress: Int) {
        _uiState.updateIfSuccess { currentState ->
            currentState.copy(progressFilter = progress)
        }
    }

    fun updateTaskProgress(task: Task, progress: Int) {
        val task = task.copy(progress = progress)
        viewModelScope.launch {
            tasksRepository.upsert(task)
        }
    }

    fun sync() {
        viewModelScope.launch {
            tasksRepository.sync()
        }
    }


    fun deleteTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.markAsDeleted(task)
        }
    }
}

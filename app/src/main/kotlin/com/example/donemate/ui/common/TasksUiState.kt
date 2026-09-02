package com.example.donemate.ui.common

import com.example.donemate.data.common.Result
import com.example.donemate.data.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed interface SortingType {
    data object TimeUpdated : SortingType
    data object ReminderDateTime : SortingType
    data object DueDateTime : SortingType
}

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

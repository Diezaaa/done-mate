package com.example.donemate.ui.common

import com.example.donemate.data.model.Task

enum class SortingType {
    TimeUpdated,
    ReminderDateTime,
    DueDateTime
}


sealed interface TasksUiState {
    data object Loading : TasksUiState
    data class Success(
        val visibleTasks: List<Task>,
        val query: String = "",
        val progressFilter: Int = -1,
        val sortingType: SortingType = SortingType.DueDateTime
    ) : TasksUiState
    data class Error(val message: String) : TasksUiState
}
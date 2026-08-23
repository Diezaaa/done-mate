package com.example.donemate.ui.common

import com.example.donemate.data.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed interface TaskFormUiState {
    data object Loading : TaskFormUiState
    data class Loaded(val task: Task, val isValidForm: Boolean = true) : TaskFormUiState
    data object Saved : TaskFormUiState
    data class Error(val message: String) : TaskFormUiState
}


// Thought: After learning the UI layer I should decide whether I should
// use the update function to update the UI state
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
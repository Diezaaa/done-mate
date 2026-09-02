package com.example.donemate.ui.screens.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.common.Result
import com.example.donemate.data.model.Task
import com.example.donemate.data.repository.offline_first.TasksRepository
import com.example.donemate.ui.common.SortingType
import com.example.donemate.ui.common.TasksUiState
import com.example.donemate.ui.common.updateIfSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<TasksUiState>(
        TasksUiState.Loading
    ).also { state ->
        tasksRepository.archivedTasks
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
            _uiState.updateIfSuccess {
                it.copy(syncingStatus = tasksRepository.sync())
            }
        }
    }


    fun deleteTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.markAsDeleted(task)
        }
    }

    fun unArchiveTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.markAsUnArchived(task.id)
        }
    }
}
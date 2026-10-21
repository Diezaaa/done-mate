package com.example.donemate.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.common.Result
import com.example.donemate.data.model.Task
import com.example.donemate.data.repository.offline_first.TasksRepository
import com.example.donemate.network.NetworkMonitor
import com.example.donemate.ui.common.SortingType
import com.example.donemate.ui.common.TasksUiState
import com.example.donemate.ui.common.searchFilterAndSortTasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {
    private val syncedOnce = MutableStateFlow(false)
    private val query = MutableStateFlow("")
    private val progressFilter = MutableStateFlow(-1)
    private val sortingType = MutableStateFlow(SortingType.DueDateTime)
    val isOnline = networkMonitor.isOnline
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val uiState: StateFlow<TasksUiState> =
        combine(
            syncedOnce,
            tasksRepository.notArchivedTasks,
            query,
            progressFilter,
            sortingType
        ) { syncedOnce, notArchivedTasks, query, progressFilter, sortingType ->
            when (notArchivedTasks) {
                is Result.Success -> {
                    if (!syncedOnce && notArchivedTasks.data.isEmpty()) {
                        TasksUiState.Loading
                    } else {
                        TasksUiState.Success(
                            visibleTasks = notArchivedTasks.data.searchFilterAndSortTasks(query, progressFilter, sortingType),
                            query = query,
                            progressFilter = progressFilter,
                            sortingType = sortingType
                        )
                    }
                }

                is Result.Error -> {
                    TasksUiState.Error(
                        message = notArchivedTasks.message
                    )
                }
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TasksUiState.Loading
            )

    fun updateQuery(newQuery: String) {
        query.value = newQuery
    }

    fun updateSortingType(newSortingType: SortingType) {
        sortingType.value = newSortingType
    }

    fun updateProgressFilter(newProgressFilter: Int) {
        progressFilter.value = newProgressFilter
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
            markSyncedOnceAsTrue()
        }
    }

    fun markSyncedOnceAsTrue() {
        syncedOnce.value = true
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.markAsDeleted(task)
        }
    }

    fun archiveTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.markAsArchived(task.id)
        }
    }
}
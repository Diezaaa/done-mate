package com.example.donemate.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.domain.remote.QuotesRepository
import com.example.donemate.domain.model.Task
import com.example.donemate.domain.sync.OfflineFirstTasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class TasksViewModel @Inject constructor(
    private val offlineFirstTasksRepository: OfflineFirstTasksRepository,
    private val quotesRepository: QuotesRepository
) : ViewModel()  {
    val tasks: StateFlow<List<Task>> =
        offlineFirstTasksRepository.getTasks()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )


    fun onTaskCheckChange(task: Task, progress: Int){
        val task = task.copy(progress = progress)
        viewModelScope.launch {
            offlineFirstTasksRepository.update(task)
        }
    }

    suspend fun syncIfOnline() = withContext(Dispatchers.IO) {
        offlineFirstTasksRepository.sync()
    }

    fun onDeleteTask(task: Task) {
        viewModelScope.launch {
            offlineFirstTasksRepository.markAsDeleted(task)
        }
    }
}

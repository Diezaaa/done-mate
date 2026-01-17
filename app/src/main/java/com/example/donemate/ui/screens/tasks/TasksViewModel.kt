package com.example.donemate.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.remote.models.Task
import com.example.donemate.domain.remote.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TasksViewModel @Inject constructor(
    private val storageService: StorageService,
) : ViewModel()  {
    val tasks = storageService.tasks

    fun onTaskCheckChange(task: Task, progress: Int){
        viewModelScope.launch {
            storageService.update(task.copy(progress = progress))
        }
    }

    fun onDeleteTask(task: Task) {
        viewModelScope.launch {
            storageService.delete(task.id)
        }
    }
}

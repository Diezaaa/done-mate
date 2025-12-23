package com.example.donemate.ui.screens.tasks

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.model.Task
import com.example.donemate.model.service.AccountService
import com.example.donemate.model.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

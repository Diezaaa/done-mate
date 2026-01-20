package com.example.donemate.ui.screens.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.domain.data.model.Task
import com.example.donemate.domain.data.remote.FirestoreService
import com.example.donemate.domain.data.repository.OfflineFirstTasksRepository
import com.example.donemate.ui.common.TaskUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val offlineFirstTasksRepository: OfflineFirstTasksRepository,
    private val storageService: FirestoreService
) : ViewModel()  {
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    var hasLoaded = false

    fun loadTask(id: String) {
        if (!hasLoaded) {
            hasLoaded = true
            viewModelScope.launch {
                val task = withContext(Dispatchers.IO) {
                     offlineFirstTasksRepository.getTaskById(id)
                }

                _uiState.value = _uiState.value.copy(
                    progress = task!!.progress,
                    title = task.title,
                    imageUri = task.imageUri,
                )
            }
        }
    }

    fun onTitleChange(title: String){
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun onProgressChange(progress: Int){
        _uiState.value = _uiState.value.copy(progress = progress)
    }

    fun onSaveTask(uri: String?, id: String){
        val taskUiState = _uiState.value
        if (uri != null){
            _uiState.value = _uiState.value.copy(imageUri = uri)
        }
        val task = Task(
            id = id,
            title = taskUiState.title,
            progress = taskUiState.progress,
            imageUri = uri
        )
        viewModelScope.launch(Dispatchers.IO) {
            offlineFirstTasksRepository.update(task)
        }
    }
}

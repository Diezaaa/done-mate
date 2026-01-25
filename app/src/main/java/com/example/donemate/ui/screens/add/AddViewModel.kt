package com.example.donemate.ui.screens.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.domain.model.Task
import com.example.donemate.domain.remote.FirestoreRepository
import com.example.donemate.domain.repository.OfflineFirstTasksRepository
import com.example.donemate.ui.common.TaskUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val storageService: FirestoreRepository,
    private val offlineFirstTasksRepository: OfflineFirstTasksRepository
) : ViewModel()  {
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String){
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun onProgressChange(progress: Int){
        _uiState.value = _uiState.value.copy(progress = progress)
    }

     fun onSaveTask(uri: String?){
        val taskUiState = _uiState.value
        val task = Task(
            id = UUID.randomUUID().toString(),
            title = taskUiState.title,
            progress = taskUiState.progress,
            imageUri = uri
        )
        viewModelScope.launch(Dispatchers.IO) {
            offlineFirstTasksRepository.insert(task)
        }
    }
}

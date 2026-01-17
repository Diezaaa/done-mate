package com.example.donemate.ui.screens.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.local.TaskPicturesRepositoryImpl
import com.example.donemate.data.remote.models.Task
import com.example.donemate.domain.remote.MediaStorage
import com.example.donemate.domain.remote.StorageService
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
    private val taskPicturesRepository: TaskPicturesRepositoryImpl,
    private val storageService: StorageService
) : ViewModel()  {
    private val _uiState = MutableStateFlow(Task())
    val uiState: StateFlow<Task> = _uiState.asStateFlow()
    var hasLoaded = false

    fun loadTask(id: String) {
        if (!hasLoaded) {
            hasLoaded = true
            viewModelScope.launch {
                val task = withContext(Dispatchers.IO) {
                    storageService.getTask(id)
                }

                _uiState.value = _uiState.value.copy(
                    id = task?.id ?: "",
                    progress = task?.progress ?: 0,
                    title = task?.title ?: "",
                    userId = task?.userId ?: ""
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

    fun onSaveTask(){
        viewModelScope.launch(Dispatchers.IO) {
            storageService.update(_uiState.value)
            taskPicturesRepository.updateTaskPictureAndDeleteOld(_uiState.value.id, _uiState.value.imageUri)
        }
    }
}

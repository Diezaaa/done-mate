package com.example.donemate.ui.screens.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.model.Task
import com.example.donemate.model.service.StorageService
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
    private val storageService: StorageService
) : ViewModel()  {
    private val _uiState = MutableStateFlow(Task())
    val uiState: StateFlow<Task> = _uiState.asStateFlow()
    var hasLoaded = false

    fun loadTask(id: String) {
        if (!hasLoaded) {
            hasLoaded = true
            viewModelScope.launch {
                // Run I/O safely on the IO dispatcher
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
        }
    }
}

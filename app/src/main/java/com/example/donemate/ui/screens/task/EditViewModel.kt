package com.example.donemate.ui.screens.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.domain.model.Task
import com.example.donemate.domain.remote.FirestoreRepository
import com.example.donemate.domain.sync.OfflineFirstTasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val offlineFirstTasksRepository: OfflineFirstTasksRepository,
    private val storageService: FirestoreRepository
) : ViewModel()  {
    private val _uiState = MutableStateFlow(Task(
        id = ""
    ))
    val uiState: StateFlow<Task> = _uiState.asStateFlow()
    var hasLoaded = false

    fun loadTask(id: String) {
        if (!hasLoaded) {
            hasLoaded = true
            viewModelScope.launch {
                val task = withContext(Dispatchers.IO) {
                     offlineFirstTasksRepository.getTaskById(id)
                }

                _uiState.value = _uiState.value.copy(
                    id = id,
                    progress = task!!.progress,
                    dateTime = task.dateTime,
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
        viewModelScope.launch(Dispatchers.IO) {
            offlineFirstTasksRepository.update(taskUiState)
        }
    }

    fun onDateTimeChange(millis: Long?){
        millis ?: return
        _uiState.value = _uiState.value.copy(dateTime = Instant.ofEpochMilli(millis))
    }
}

package com.example.donemate.ui.screens.add

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donemate.data.local.TaskPicturesRepositoryImpl
import com.example.donemate.data.local.models.TaskPicture
import com.example.donemate.data.remote.models.Task
import com.example.donemate.domain.remote.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val storageService: StorageService,
    private val picturesRepositoryImpl: TaskPicturesRepositoryImpl
) : ViewModel()  {
    private val _uiState = MutableStateFlow(Task())
    val uiState: StateFlow<Task> = _uiState.asStateFlow()

    fun onTitleChange(title: String){
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun onProgressChange(progress: Int){
        _uiState.value = _uiState.value.copy(progress = progress)
    }

    fun onSaveTask(uri: String?){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val id = storageService.save(_uiState.value, null)
                if (uri != null){ picturesRepositoryImpl.insert(TaskPicture(0, id, uri))}
            } catch (e: Exception) {
                Log.e("DEBUG", "Failed to save: $e")
            }
        }

    }
}

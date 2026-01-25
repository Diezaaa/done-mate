package com.example.donemate.ui.common

import com.example.donemate.domain.model.Progress

data class TaskUiState(
    val title: String = "",
    val progress: Int = Progress.NOT_STARTED.value,
    var imageUri: String? = null,
)

package com.example.donemate.data.remote.models

import com.example.donemate.data.local.models.TaskEntity
import com.example.donemate.domain.model.Progress
import com.google.firebase.firestore.DocumentId

// Task data class
data class NetworkTask(
    @DocumentId val id: String = "",
    val title: String = "",
    val progress: Int = Progress.NOT_STARTED.value,
    val userId: String = "",
    var path: String? = null,
    var updatedAt: Long = 0L
)

fun NetworkTask.asEntity() = TaskEntity(
    id = id,
    title = title,
    imageUri = null,
    progress = progress,
    userId = userId,
    updatedAt = updatedAt,
    needsSync = false
)

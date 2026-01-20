package com.example.donemate.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.donemate.domain.data.model.Progress
import com.example.donemate.domain.data.model.Task

@Entity
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val progress: Int = Progress.NOT_STARTED.value,
    val userId: String,
    var imageUri: String? = null,
    var updatedAt: Long,
    val isDeleted: Boolean = false,
    val needsSync: Boolean
)

fun TaskEntity.asExternalModel() = Task(
    id = id,
    title = title,
    imageUri = imageUri,
    progress = progress
)
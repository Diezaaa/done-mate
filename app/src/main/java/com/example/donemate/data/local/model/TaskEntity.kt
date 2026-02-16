package com.example.donemate.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.donemate.domain.model.Progress
import com.example.donemate.domain.model.Task
import java.time.Instant
import java.time.LocalDate
import kotlin.String

@Entity
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val progress: Int = Progress.NOT_STARTED.value,
    val userId: String,
    val dateTime: Instant? = null,
    var imageUri: String? = null,
    var updatedAt: Long,
    val isDeleted: Boolean = false,
    val needsSync: Boolean
)

fun TaskEntity.asExternalModel() = Task(
    id = id,
    title = title,
    dateTime = dateTime,
    imageUri = imageUri,
    progress = progress
)

fun Task.asEntity(userId: String) = TaskEntity(
    id = id,
    title = title,
    progress = progress,
    userId = userId,
    dateTime = dateTime,
    imageUri = imageUri,
    updatedAt = System.currentTimeMillis(),
    isDeleted = false,
    needsSync = true
)


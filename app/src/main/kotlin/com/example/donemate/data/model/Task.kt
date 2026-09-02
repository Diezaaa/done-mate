package com.example.donemate.data.model

import com.example.donemate.data.remote.model.RemoteTask
import com.example.donemate.data.database.model.TaskEntity
import java.time.Instant
import java.util.UUID

enum class Progress(val value: Int, val label: String) {
    NOT_STARTED(0, "Not Started"),
    IN_PROGRESS(1, "In Progress"),
    COMPLETED(2, "Completed");

    companion object {
        fun fromInt(value: Int): Progress =
            entries.firstOrNull { it.value == value } ?: NOT_STARTED

        fun labelFromInt(value: Int): String = fromInt(value).label
    }
}

data class Task (
    val id: String =  UUID.randomUUID().toString(),
    val title: String = "",
    val reminderDateTime: Instant? = null,
    val dueDateTime: Instant? = null,
    val progress: Int = Progress.NOT_STARTED.value,
    var imageUri: String? = null,
    val updateTime: Instant? = null,
    val description: String? = ""
)

fun RemoteTask.asEntity() = TaskEntity(
    id = id,
    title = title,
    imageUri = null,
    progress = progress,
    userId = userId,
    updatedAt = updatedAt,
    needsSync = false,
    reminderDateTime = if (reminderDateTime != null) Instant.ofEpochMilli(reminderDateTime) else null,
    dueDateTime = if (dueDateTime != null) Instant.ofEpochMilli(dueDateTime) else null,
    description = description,
    isArchived = wasArchived
)

fun TaskEntity.asExternalModel() = Task(
    id = id,
    title = title,
    reminderDateTime = reminderDateTime,
    dueDateTime = dueDateTime,
    imageUri = imageUri,
    progress = progress,
    updateTime = Instant.ofEpochMilli(updatedAt),
    description = description,
)

fun Task.asEntity(userId: String) = TaskEntity(
    id = id,
    title = title,
    progress = progress,
    reminderDateTime = reminderDateTime,
    dueDateTime = dueDateTime,
    userId = userId,
    imageUri = imageUri,
    updatedAt = System.currentTimeMillis(),
    isDeleted = false,
    needsSync = true,
    description = description
)
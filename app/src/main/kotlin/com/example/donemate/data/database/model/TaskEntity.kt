package com.example.donemate.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.donemate.data.model.Progress
import kotlin.String
import kotlin.time.Instant

@Entity
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val progress: Int = Progress.NOT_STARTED.value,
    val userId: String,
    val reminderDateTime: Instant? = null,
    val dueDateTime: Instant? = null,
    val imageUri: String? = null,
    val updatedAt: Long,
    val isDeleted: Boolean = false,
    val needsSync: Boolean,
    val description: String?,
    val isArchived: Boolean
)
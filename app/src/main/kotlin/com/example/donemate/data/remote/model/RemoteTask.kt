package com.example.donemate.data.remote.model

import com.example.donemate.data.model.Progress
import com.google.firebase.firestore.DocumentId

data class RemoteTask(
    @DocumentId val id: String = "",
    val title: String = "",
    val progress: Int = Progress.NOT_STARTED.value,
    val userId: String = "",
    val path: String? = null,
    val updatedAt: Long = 0L,
    val reminderDateTime: Long? = null,
    val dueDateTime: Long? = null,
    val description: String? = null,
    val wasArchived: Boolean = false
)
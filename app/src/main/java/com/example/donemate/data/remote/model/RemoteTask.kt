package com.example.donemate.data.remote.model

import com.example.donemate.data.model.Progress
import com.google.firebase.firestore.DocumentId

// Task data class
data class RemoteTask(
    @DocumentId val id: String = "",
    val title: String = "",
    val progress: Int = Progress.NOT_STARTED.value,
    val userId: String = "",
    var path: String? = null,
    var updatedAt: Long = 0L,
    val reminderDateTime: Long? = null,
    val dueDateTime: Long? = null,
    val description: String? = null
)
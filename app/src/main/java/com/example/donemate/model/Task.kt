package com.example.donemate.model

import com.google.firebase.firestore.DocumentId

enum class Progress(val value: Int, val label: String) {
    NOT_STARTED(0, "Not Started"),
    IN_PROGRESS(1, "In Progress"),
    COMPLETED(2, "Completed");

    companion object {
        fun fromInt(value: Int): Progress =
            Progress.entries.firstOrNull { it.value == value } ?: NOT_STARTED

        fun labelFromInt(value: Int): String = fromInt(value).label
    }
}

// Task data class
data class Task(
    @DocumentId val id: String = "",
    val title: String = "",
    val progress: Int = Progress.NOT_STARTED.value,
    val userId: String = ""
)
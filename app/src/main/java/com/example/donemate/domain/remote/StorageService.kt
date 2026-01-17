package com.example.donemate.domain.remote

import android.net.Uri
import com.example.donemate.data.remote.models.Task
import kotlinx.coroutines.flow.Flow

interface StorageService {
    val tasks: Flow<List<Task>>
    suspend fun getTask(taskId: String): Task?
    suspend fun save(task: Task, uri: String?): String
    suspend fun update(task: Task)
    suspend fun delete(taskId: String)
}
package com.example.donemate.domain.sync

import com.example.donemate.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface OfflineFirstTasksRepository {
    suspend fun insert(task: Task)

    suspend fun delete(id: String)

    suspend fun update(newTask: Task)

    fun deleteIfPrivateFile(uri: String)

    suspend fun sync()

    fun getTasks(): Flow<List<Task>>
    suspend fun getTaskById(id: String): Task?

    suspend fun markAsDeleted(task: Task)
}
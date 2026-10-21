package com.example.donemate.data.repository.offline_first

import com.example.donemate.data.common.Result
import com.example.donemate.data.model.Task
import kotlinx.coroutines.flow.Flow

interface TasksRepository {
    val notArchivedTasks: Flow<Result<List<Task>>>
    val tasks : Flow<Result<List<Task>>>
    val archivedTasks: Flow<Result<List<Task>>>
    suspend fun upsert(task: Task): Result<Unit>
    suspend fun markAsArchived(id: String): Result<Unit>
    suspend fun markAsUnArchived(id: String): Result<Unit>
    fun deleteIfPrivateFile(uri: String)
    suspend fun sync(): Result<Unit>
    suspend fun getTaskById(id: String): Result<Task>
    suspend fun markAsDeleted(task: Task): Result<Unit>
    suspend fun deleteAll(): Result<Unit>
}
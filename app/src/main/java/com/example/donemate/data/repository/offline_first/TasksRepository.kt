package com.example.donemate.data.repository.offline_first

import com.example.donemate.data.common.Result
import com.example.donemate.data.model.Task
import kotlinx.coroutines.flow.Flow

interface TasksRepository {
    suspend fun upsert(task: Task): Result<Unit>

    suspend fun delete(id: String): Result<Unit>


    fun deleteIfPrivateFile(uri: String)

    suspend fun sync(): Result<Unit>
    fun isNetworkAvailable(): Boolean
    val tasks: Flow<Result<List<Task>>>
    suspend fun getTaskById(id: String): Result<Task>

    suspend fun markAsDeleted(task: Task): Result<Unit>
    suspend fun deleteAll(): Result<Unit>

}
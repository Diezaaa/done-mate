package com.example.donemate.data.database.data_sources

import com.example.donemate.data.database.model.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TasksLocalDataSource {
    suspend fun upsert(taskEntity: TaskEntity)

    suspend fun markAsDeleted(
        taskId: String,
        deletedAt: Long
    )
    suspend fun markAsArchived(id: String, archivedAt: Long)
    fun getTasks(): Flow<List<TaskEntity>>

    suspend fun deleteAll()
    suspend fun markAsUnArchived(id: String, archivedAt: Long)

    suspend fun getTask(id: String): TaskEntity?


    suspend fun getTasksNeedingSync(): List<TaskEntity>


    suspend fun markAsSyncedIfUnchanged(
        id: String,
        expectedUpdatedAt: Long,
    )

    fun getArchivedTasks(): Flow<List<TaskEntity>>

    fun getNotArchivedTasks(): Flow<List<TaskEntity>>

    suspend fun delete(taskEntity: TaskEntity)

    suspend fun getTasksIncludingDeleted(): List<TaskEntity>
}
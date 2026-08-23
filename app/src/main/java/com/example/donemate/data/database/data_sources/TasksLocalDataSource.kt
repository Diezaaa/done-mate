package com.example.donemate.data.database.data_sources

import com.example.donemate.data.database.model.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TasksLocalDataSource {
    suspend fun upsert(taskEntity: TaskEntity)

    suspend fun markAsDeleted(taskId: String)


    suspend fun deleteAll()



    suspend fun getTask(id: String): TaskEntity?


    suspend fun getTasksNeedingSync(): List<TaskEntity>


    suspend fun markUploaded(id: String)


    fun getNotDeletedTasks(): Flow<List<TaskEntity>>

    suspend fun delete(taskEntity: TaskEntity)

    suspend fun getTasksIncludingDeleted(): List<TaskEntity>
}
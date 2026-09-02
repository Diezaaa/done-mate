package com.example.donemate.data.remote

import com.example.donemate.data.remote.model.RemoteTask

interface TasksRemoteDataSource {
    suspend fun getTasks(userId: String): List<RemoteTask>
    suspend fun getTask(taskId: String): RemoteTask?
    suspend fun save(task: RemoteTask, userId: String): String
    suspend fun update(task: RemoteTask)
    suspend fun delete(taskId: String)
}
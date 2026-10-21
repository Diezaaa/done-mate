package com.example.donemate.data.remote

import com.example.donemate.data.remote.model.RemoteTask

interface TasksRemoteDataSource {
    suspend fun getTasks(userId: String): List<RemoteTask>
    suspend fun save(task: RemoteTask, userId: String): String
    suspend fun delete(taskId: String)
}
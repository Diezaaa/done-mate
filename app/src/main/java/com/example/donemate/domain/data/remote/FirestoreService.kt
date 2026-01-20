package com.example.donemate.domain.data.remote

import com.example.donemate.data.remote.models.NetworkTask
import kotlinx.coroutines.flow.Flow

interface FirestoreService {
    suspend fun getTasks(): List<NetworkTask>
    suspend fun getTask(taskId: String): NetworkTask?
    suspend fun save(task: NetworkTask): String
    suspend fun update(task: NetworkTask)
    suspend fun delete(taskId: String)
}
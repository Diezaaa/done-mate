package com.example.donemate.data.remote

import android.net.Uri
import android.util.Log
import com.example.donemate.data.remote.models.Task
import com.example.donemate.domain.remote.AccountService
import com.example.donemate.domain.remote.StorageService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore, private val auth: AccountService) :
    StorageService {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val tasks: Flow<List<Task>>
        get() =
            auth.currentUser.flatMapLatest { user ->
                firestore
                    .collection("tasks")
                    .whereEqualTo("userId", user?.id)
                    .dataObjects()
            }

    override suspend fun getTask(taskId: String): Task? =
        firestore.collection("tasks").document(taskId).get().await().toObject()

    override suspend fun save(task: Task, uri: String?): String {
        val taskWithUserId = task.copy(userId = auth.currentUserId)
        val taskToSave = if (!uri.isNullOrBlank()) {
            taskWithUserId.copy(imageUri = uri)
        } else {
            taskWithUserId
        }

        val docRef = firestore.collection("tasks").document()  // Generate ID
        val id = docRef.id

        docRef.set(taskToSave)

        return id
    }

    override suspend fun update(task: Task): Unit {
        firestore.collection("tasks").document(task.id).set(task).await()
    }

    override suspend fun delete(taskId: String) {
        firestore.collection("tasks").document(taskId).delete().await()
    }
}
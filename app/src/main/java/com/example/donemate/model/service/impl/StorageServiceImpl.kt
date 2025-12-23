package com.example.donemate.model.service.impl

import com.example.donemate.model.Task
import com.example.donemate.model.service.AccountService
import com.example.donemate.model.service.StorageService
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
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

    override suspend fun save(task: Task): String {
        val taskWithUserId = task.copy(userId = auth.currentUserId)
        return firestore.collection("tasks").add(taskWithUserId).await().id
    }

    override suspend fun update(task: Task): Unit {
        firestore.collection("tasks").document(task.id).set(task).await()
    }

    override suspend fun delete(taskId: String) {
        firestore.collection("tasks").document(taskId).delete().await()
    }
}
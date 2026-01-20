package com.example.donemate.data.remote

import com.example.donemate.data.remote.models.NetworkTask
import com.example.donemate.domain.data.remote.AccountService
import com.example.donemate.domain.data.remote.FirestoreService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore, private val auth: AccountService) :
    FirestoreService {
    override suspend fun getTasks(): List<NetworkTask> {
        val snap = firestore.collection("tasks")
            .whereEqualTo("userId", auth.currentUserId)
            .get()
            .await()

        return snap.toObjects(NetworkTask::class.java)
    }

    override suspend fun getTask(taskId: String): NetworkTask? =
        firestore.collection("tasks").document(taskId).get().await().toObject()

    override suspend fun save(task: NetworkTask): String {
        val taskWithUserId = task.copy(userId = auth.currentUserId)

        val docRef = firestore.collection("tasks").document(task.id)
        val id = docRef.id

        docRef.set(taskWithUserId)

        return id
    }

    override suspend fun update(task: NetworkTask) {
        val taskToSave = task.copy()

        firestore
            .collection("tasks")
            .document(taskToSave.id)
            .set(taskToSave)
            .await()
    }

    override suspend fun delete(taskId: String) {
        firestore.collection("tasks").document(taskId).delete().await()
    }
}
package com.example.donemate.data.remote

import com.example.donemate.data.remote.model.RemoteTask
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DefaultTasksRemoteDataSource @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
): TasksRemoteDataSource {
    override suspend fun getTasks(userId: String): List<RemoteTask> {
        val snap = firebaseFirestore.collection("tasks")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return snap.toObjects(RemoteTask::class.java)
    }

    override suspend fun getTask(taskId: String): RemoteTask? =
        firebaseFirestore.collection("tasks").document(taskId).get().await().toObject()

    override suspend fun save(task: RemoteTask, userId: String): String {
        val taskWithUserId = task.copy(userId = userId)

        val docRef = firebaseFirestore.collection("tasks").document(task.id)
        val id = docRef.id

        docRef.set(taskWithUserId)

        return id
    }

    override suspend fun update(task: RemoteTask) {
        val taskToSave = task.copy()

        firebaseFirestore
            .collection("tasks")
            .document(taskToSave.id)
            .set(taskToSave)
            .await()
    }

    override suspend fun delete(taskId: String) {
        firebaseFirestore.collection("tasks").document(taskId).delete().await()
    }
}
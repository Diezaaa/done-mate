package com.example.donemate.data.remote

import com.example.donemate.data.remote.model.RemoteTask
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DefaultTasksRemoteDataSource @Inject constructor(
    firebaseFirestore: FirebaseFirestore
) : TasksRemoteDataSource {
    private val collectionReference = firebaseFirestore.collection("tasks")

    override suspend fun getTasks(userId: String): List<RemoteTask> {
        val query = collectionReference
            .whereEqualTo("userId", userId)
        val snap = ServerReads.get(query).await()

        return snap.toObjects(RemoteTask::class.java)
    }

    override suspend fun save(task: RemoteTask, userId: String): String {
        val taskWithUserId = task.copy(userId = userId)

        val docRef = collectionReference.document(taskWithUserId.id)
        val id = docRef.id

        docRef.set(taskWithUserId).await()

        return id
    }

    override suspend fun delete(taskId: String) {
        collectionReference.document(taskId).delete().await()
    }
}
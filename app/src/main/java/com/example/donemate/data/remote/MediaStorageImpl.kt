package com.example.donemate.data.remote

import android.net.Uri
import com.example.donemate.domain.remote.MediaStorage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await
import java.util.UUID

class MediaStorageImpl @Inject constructor(private val storage : FirebaseStorage, private val firestore: FirebaseFirestore) : MediaStorage {
    override suspend fun setTaskImage(taskId: String, byteArray: ByteArray): Uri {
        val storageRef = storage.reference
        val tasksRef = storageRef.child("/tasks").child("${UUID.randomUUID()}.png")
        tasksRef.putBytes(byteArray).await()
        return tasksRef.downloadUrl.await()
    }
}
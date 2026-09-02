package com.example.donemate.data.remote

import com.google.firebase.storage.FirebaseStorage
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await
import java.util.UUID

class DefaultTaskImagesRemoteDataSource @Inject constructor(private val storage : FirebaseStorage) :
    TaskImagesRemoteDataSource {
    override suspend fun uploadPicture(taskId: String, byteArray: ByteArray): String {
        val path = "tasks/${UUID.randomUUID()}.png"
        val ref = storage.reference.child(path)
        ref.putBytes(byteArray).await()
        return path
    }

    override suspend fun downloadPicture(path: String): ByteArray {
        val ref = storage.reference.child(path)
        return ref.getBytes(10L * 1024 * 1024).await()
    }

    override suspend fun deletePicture(path: String) {
        val ref = storage.reference.child(path)
        ref.delete().await()
    }

}
package com.example.donemate.data.remote

import com.google.firebase.storage.FirebaseStorage
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await
import java.util.UUID

// TODO: Consider adding error handling here. Maybe repository is more suitable for error handling. Read about the data layer again
class DefaultTaskImagesRemoteDataSource @Inject constructor(private val firebaseStorage: FirebaseStorage) :
    TaskImagesRemoteDataSource {
    override suspend fun uploadPicture(byteArray: ByteArray): String {
        val path = "tasks/${UUID.randomUUID()}.png"
        val ref = firebaseStorage.reference.child(path)
        ref.putBytes(byteArray).await()
        return path
    }

    override suspend fun downloadPicture(path: String): ByteArray {
        val ref = firebaseStorage.reference.child(path)
        return ref.getBytes(10L * 1024 * 1024).await()
    }

    override suspend fun deletePicture(path: String) {
        val ref = firebaseStorage.reference.child(path)
        ref.delete().await()
    }
}
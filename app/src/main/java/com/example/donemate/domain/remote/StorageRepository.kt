package com.example.donemate.domain.remote

interface StorageRepository {
    suspend fun uploadPicture(taskId: String, byteArray: ByteArray): String

    suspend fun downloadPicture(path: String): ByteArray

    suspend fun deletePicture(path: String)

}
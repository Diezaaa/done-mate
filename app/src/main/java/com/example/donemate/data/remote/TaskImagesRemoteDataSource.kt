package com.example.donemate.data.remote

interface TaskImagesRemoteDataSource {
    suspend fun uploadPicture(taskId: String, byteArray: ByteArray): String

    suspend fun downloadPicture(path: String): ByteArray

    suspend fun deletePicture(path: String)

}
package com.example.donemate.domain.data.remote

import android.net.Uri

interface StorageNetwork {
    suspend fun uploadPicture(taskId: String, byteArray: ByteArray): String

    suspend fun downloadPicture(path: String): ByteArray

    suspend fun deletePicture(path: String)

}
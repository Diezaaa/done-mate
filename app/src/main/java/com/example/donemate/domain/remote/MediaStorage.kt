package com.example.donemate.domain.remote

import android.net.Uri

interface MediaStorage {
    suspend fun setTaskImage(taskId: String, byteArray: ByteArray): Uri
}
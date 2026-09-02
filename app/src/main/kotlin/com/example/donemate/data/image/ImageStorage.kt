package com.example.donemate.data.image

interface ImageStorage {
    suspend fun write(byteArray: ByteArray): String
    suspend fun read(uri: String): ByteArray
}
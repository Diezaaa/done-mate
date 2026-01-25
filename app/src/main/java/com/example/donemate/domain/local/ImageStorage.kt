package com.example.donemate.domain.local

interface ImageStorage {
    fun write(byteArray: ByteArray): String
    fun read(uri: String): ByteArray
}
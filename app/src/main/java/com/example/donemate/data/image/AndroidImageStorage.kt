package com.example.donemate.data.image

import android.app.Application
import android.content.ContentResolver
import androidx.core.net.toUri
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File

@Singleton
class AndroidImageStorage @Inject constructor(
    private val application: Application,
    private val ioDispatcher: CoroutineDispatcher
) : ImageStorage {

    override suspend fun write(byteArray: ByteArray): String = withContext(ioDispatcher) {
            val file = File(
                application.filesDir,
                "IMG_${System.currentTimeMillis()}.jpg"
            )
            file.writeBytes(byteArray)
            file.toUri().toString()
    }

    override suspend fun read(uri: String): ByteArray = withContext(ioDispatcher) {
        val parsed = uri.toUri()
        when (parsed.scheme) {
            ContentResolver.SCHEME_FILE ->
                File(requireNotNull(parsed.path)).readBytes()

            ContentResolver.SCHEME_CONTENT ->
                application.contentResolver.openInputStream(parsed)?.use {
                    it.readBytes()
                } ?: error("Cannot open URI: $uri")

            else -> error("Unsupported URI: $uri")
        }
    }
}
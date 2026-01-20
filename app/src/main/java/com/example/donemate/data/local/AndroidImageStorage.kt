package com.example.donemate.data.local

import android.app.Application
import android.content.ContentResolver
import com.example.donemate.domain.data.local.ImageStorage
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.io.File
import androidx.core.net.toUri

@Singleton
class AndroidImageStorage @Inject constructor(
    private val context: Application
) : ImageStorage {

    override fun write(byteArray: ByteArray): String {
        val file = File(
            context.filesDir,
            "IMG_${System.currentTimeMillis()}.jpg"
        )
        file.writeBytes(byteArray)
        return file.toUri().toString()
    }

    override fun read(uri: String): ByteArray {
        val parsed = uri.toUri()
        return when (parsed.scheme) {
            ContentResolver.SCHEME_FILE ->
                File(requireNotNull(parsed.path)).readBytes()

            ContentResolver.SCHEME_CONTENT ->
                context.contentResolver.openInputStream(parsed)?.use {
                    it.readBytes()
                } ?: error("Cannot open URI: $uri")

            else -> error("Unsupported URI: $uri")
        }
    }
}

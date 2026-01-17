package com.example.donemate.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.donemate.domain.remote.MediaStorage
import com.example.donemate.domain.remote.StorageService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val mediaStorage: MediaStorage,
    private val storage: StorageService
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "SyncWorker started")

        val taskId = inputData.getString(KEY_TASK_ID)
        val path = inputData.getString(KEY_IMAGE_PATH)

        if (taskId == null || path == null) {
            Log.e(TAG, "Missing input data. taskId=$taskId, path=$path")
            return Result.failure()
        }

        return try {
            val bytes = readBytesFromUri(
                context = applicationContext,
                uri = path.toUri()
            )

            val ref = mediaStorage.setTaskImage(taskId, bytes)
            val task = storage.getTask(taskId)

            task!!.imageUri = ref.toString()
            storage.update(task)

            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "SyncWorker failed", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "SyncWorker"
        const val KEY_TASK_ID = "TASK_ID"
        const val KEY_IMAGE_PATH = "IMAGE_PATH"
    }
}


fun readBytesFromUri(context: Context, uri: Uri): ByteArray { return context.contentResolver.openInputStream(uri)!!.readBytes() }
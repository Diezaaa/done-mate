package com.example.donemate.data.local

import android.app.Application
import android.util.Log
import androidx.core.net.toUri
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.example.donemate.data.local.models.TaskPicture
import com.example.donemate.domain.remote.MediaStorage
import com.example.donemate.workers.SyncWorker
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// ADD AN INTERFACE TO IMPL
class TaskPicturesRepositoryImpl @Inject constructor(private val mediaStorage: MediaStorage, private val taskPictureDao: TaskPictureDao, private val application: Application) {
     suspend fun insert(taskPicture: TaskPicture){
        taskPictureDao.insert(taskPicture)
    }

     suspend fun update(taskPicture: TaskPicture){
        taskPictureDao.update(taskPicture)
    }

     suspend fun delete(taskPicture: TaskPicture){
        taskPictureDao.delete(taskPicture)
    }


    suspend fun updateTaskPictureAndDeleteOld(taskId: String, newPicture: String?) {
        val oldPicture = taskPictureDao.getTaskPictureUri(taskId)
        taskPictureDao.updateTaskPictureUri(taskId, newPicture)

        deleteIfPrivateFile(oldPicture)
    }

    private fun deleteIfPrivateFile(picture: String?) {
        if (picture.isNullOrBlank()) return

        val uri = picture.toUri()
        when (uri.scheme) {
            null -> File(picture).delete()                 // raw absolute path
            "file" -> File(uri.path!!).delete()            // file://...
        }
    }
}


package com.example.donemate.data.repository.offline_first

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.donemate.alarm.AlarmScheduler
import com.example.donemate.data.common.Result
import com.example.donemate.data.common.safeCall
import com.example.donemate.data.database.data_sources.TasksLocalDataSource
import com.example.donemate.data.image.ImageStorage
import com.example.donemate.data.model.Task
import com.example.donemate.data.remote.model.RemoteTask
import com.example.donemate.data.model.asEntity
import com.example.donemate.data.model.asExternalModel
import com.example.donemate.data.remote.TaskImagesRemoteDataSource
import com.example.donemate.data.remote.TasksRemoteDataSource
import com.example.donemate.data.repository.remote.AccountRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

private const val TAG = "OfflineFirstTasksRepository"

class OfflineFirstTasksRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val accountRepository: AccountRepository,
    private val imageStorage: ImageStorage,
    private val alarmScheduler: AlarmScheduler,
    private val tasksLocalDataSource: TasksLocalDataSource,
    private val tasksRemoteDataSource: TasksRemoteDataSource,
    private val taskImagesRemoteDataSource: TaskImagesRemoteDataSource,
) : TasksRepository {
    override suspend fun upsert(task: Task): Result<Unit> {
        return safeCall("insert", TAG) {
            val oldTaskUri = tasksLocalDataSource.getTask(task.id)?.imageUri
            if (oldTaskUri != task.imageUri && oldTaskUri != null) {
                deleteIfPrivateFile(oldTaskUri)
            }

            tasksLocalDataSource.upsert(task.asEntity(accountRepository.getUserId()!!))
        }
    }


    override suspend fun delete(id: String): Result<Unit> {
        return safeCall("delete", TAG) {
            val task = tasksLocalDataSource.getTask(id)
            task.let { tasksLocalDataSource.markAsDeleted(it!!.id) }
        }
    }


    override fun deleteIfPrivateFile(uri: String) {
        if (uri.isBlank()) return

        File(uri).delete()
    }

    override val tasks: Flow<Result<List<Task>>> = flow {
        try {
            tasksLocalDataSource.getNotDeletedTasks().collect { entities ->
                emit(Result.Success(entities.map { it.asExternalModel() }))
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.message.orEmpty(), exception)
            emit(Result.Error("Something went wrong. Please try again."))
        }
    }

    // סנכרון בין השרת למסד הנתונים המקומי
    override suspend fun sync(): Result<Unit> {
        val isOnline = isNetworkAvailable()
        if (!isOnline) {
            return Result.Error("Something went wrong. Check internet connection")
        }
        return safeCall("sync", TAG) {
            val allLocal = tasksLocalDataSource.getTasksIncludingDeleted()
            val needingSync = tasksLocalDataSource.getTasksNeedingSync()
            val remoteTasks = tasksRemoteDataSource.getTasks(accountRepository.getUserId()!!)

            val localMap = allLocal.associateBy { it.id }
            val remoteMap = remoteTasks.associateBy { it.id }

            // Remote → Local
            remoteMap.values.forEach { remote ->
                val local = localMap[remote.id]


                if (local == null) {
                    var taskEntity = remote.asEntity()
                    remote.path?.let {
                        val image = taskImagesRemoteDataSource.downloadPicture(remote.path!!)
                        val uri = imageStorage.write(image)
                        taskEntity = taskEntity.copy(imageUri = uri)
                    }
                    tasksLocalDataSource.upsert(taskEntity)
                } else if (remote.updatedAt > local.updatedAt) {
                    var taskEntity = remote.asEntity()
                    remote.path?.let {
                        val image = taskImagesRemoteDataSource.downloadPicture(remote.path!!)
                        val uri = imageStorage.write(image)
                        taskEntity = taskEntity.copy(imageUri = uri)
                    }
                    tasksLocalDataSource.upsert(taskEntity)
                }
            }

            // Local → Remote
            needingSync.forEach { local ->
                val remote = remoteMap[local.id]

                if (local.isDeleted) {
                    tasksRemoteDataSource.delete(local.id)
                    tasksLocalDataSource.delete(local)
                    return@forEach
                }

                var updatedRemote = RemoteTask(
                    id = local.id,
                    title = local.title,
                    progress = local.progress,
                    userId = local.userId,
                    path = remote?.path,
                    updatedAt = local.updatedAt,
                    reminderDateTime = local.reminderDateTime?.toEpochMilli(),
                    dueDateTime = local.dueDateTime?.toEpochMilli(),
                    description = local.description
                )

                if (remote == null) {
                    if (local.imageUri != null) {
                        val uri = taskImagesRemoteDataSource.uploadPicture(
                            local.id,
                            imageStorage.read(local.imageUri)
                        )
                        updatedRemote = updatedRemote.copy(path = uri)
                    }
                    tasksRemoteDataSource.save(updatedRemote, accountRepository.getUserId()!!)
                    tasksLocalDataSource.markUploaded(local.id)
                } else if (local.updatedAt > remote.updatedAt) {
                    if (local.imageUri != null) {
                        val uri = taskImagesRemoteDataSource.uploadPicture(
                            local.id,
                            imageStorage.read(local.imageUri)
                        )
                        updatedRemote = updatedRemote.copy(path = uri)
                    }
                    tasksRemoteDataSource.update(updatedRemote)
                    tasksLocalDataSource.markUploaded(local.id)
                }
            }

            localMap.values.forEach { local ->
                val remote = remoteMap[local.id]

                if (remote == null && !local.needsSync) {
                    tasksLocalDataSource.delete(local)
                }
            }

            val tasks = tasksLocalDataSource.getNotDeletedTasks().first()
            tasks.forEach { task ->
                if (task.reminderDateTime != null) {
                    alarmScheduler.setReminder(task.reminderDateTime.toEpochMilli(), task.id)
                }
            }
        }
    }

    override suspend fun markAsDeleted(task: Task): Result<Unit> {
        return safeCall("markAsDeleted", TAG) {
            tasksLocalDataSource.markAsDeleted(task.id)
        }
    }

    override suspend fun getTaskById(id: String): Result<Task> {
        return safeCall("getTaskById", TAG) {
            tasksLocalDataSource.getTask(id)!!.asExternalModel()
        }
    }

    // TODO: I'm a bit concerned about the correctness. I should check official docs
    override fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    override suspend fun deleteAll(): Result<Unit> {
        return safeCall("deleteAll", TAG) {
            tasksLocalDataSource.deleteAll()
        }
    }

}
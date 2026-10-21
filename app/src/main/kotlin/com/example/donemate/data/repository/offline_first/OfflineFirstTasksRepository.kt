package com.example.donemate.data.repository.offline_first

import com.example.donemate.alarm.AlarmScheduler
import com.example.donemate.data.common.Result
import com.example.donemate.data.common.safeCall
import com.example.donemate.data.database.data_sources.TasksLocalDataSource
import com.example.donemate.data.image.TaskImagesLocalDataSource
import com.example.donemate.data.model.Task
import com.example.donemate.data.remote.model.RemoteTask
import com.example.donemate.data.model.asEntity
import com.example.donemate.data.model.asExternalModel
import com.example.donemate.data.remote.TaskImagesRemoteDataSource
import com.example.donemate.data.remote.TasksRemoteDataSource
import com.example.donemate.data.repository.remote.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

private const val TAG = "OfflineFirstTasksRepository"

class OfflineFirstTasksRepository @Inject constructor(
    private val accountRepository: AccountRepository,
    private val taskImagesLocalDataSource: TaskImagesLocalDataSource,
    private val alarmScheduler: AlarmScheduler,
    private val tasksLocalDataSource: TasksLocalDataSource,
    private val tasksRemoteDataSource: TasksRemoteDataSource,
    private val taskImagesRemoteDataSource: TaskImagesRemoteDataSource
) : TasksRepository {
    override suspend fun upsert(task: Task): Result<Unit> {
        return safeCall("upsert", TAG) {
            val oldTaskUri = tasksLocalDataSource.getTask(task.id)?.imageUri
            if (oldTaskUri != task.imageUri && oldTaskUri != null) {
                deleteIfPrivateFile(oldTaskUri)
            }

            tasksLocalDataSource.upsert(task.asEntity(accountRepository.getUserId()!!))
        }
    }

    override suspend fun markAsArchived(id: String): Result<Unit> {
        return safeCall("markAsArchived", TAG) {
            tasksLocalDataSource.markAsArchived(id, System.currentTimeMillis())
        }
    }

    override suspend fun markAsUnArchived(id: String): Result<Unit> {
        return safeCall("markAsUnArchived", TAG) {
            tasksLocalDataSource.markAsUnArchived(id, System.currentTimeMillis())
        }
    }

    override fun deleteIfPrivateFile(uri: String) {
        if (uri.isBlank()) return

        File(uri).delete()
    }

    override val tasks: Flow<Result<List<Task>>> =
        tasksLocalDataSource.getTasks()
            .map { entities ->
                val result: Result<List<Task>> =
                    Result.Success(entities.map { it.asExternalModel() })
                result
            }
            .catch {
                emit(Result.Error("Something went wrong."))
            }

    override val notArchivedTasks: Flow<Result<List<Task>>> =
        tasksLocalDataSource.getNotArchivedTasks()
            .map { entities ->
                val result: Result<List<Task>> =
                    Result.Success(entities.map { it.asExternalModel() })

                result
            }
            .catch {
                emit(Result.Error("Something went wrong."))
            }

    override val archivedTasks: Flow<Result<List<Task>>> =
        tasksLocalDataSource.getArchivedTasks()
            .map { entities ->
                val result: Result<List<Task>> =
                    Result.Success(entities.map { it.asExternalModel() })
                result
            }
            .catch {
                emit(Result.Error("Something went wrong."))
            }



    /** Syncing tasks using last-write wins and soft deletes */
    override suspend fun sync(): Result<Unit> {
        return safeCall("sync", TAG) {
            val remoteTasks = tasksRemoteDataSource.getTasks(accountRepository.getUserId()!!)
            val allLocal = tasksLocalDataSource.getTasksIncludingDeleted()
            val localTasks = tasksLocalDataSource.getTasks().first()
            val needingSync = tasksLocalDataSource.getTasksNeedingSync()
            val localMap = allLocal.associateBy { it.id }
            val remoteMap = remoteTasks.associateBy { it.id }

            // Remote → Local
            remoteMap.values.forEach { remote ->
                val local = localMap[remote.id]

                if (local == null || remote.updatedAt > local.updatedAt) {
                    var taskEntity = remote.asEntity()
                    remote.path?.let {
                        val image = taskImagesRemoteDataSource.downloadPicture(remote.path)
                        val uri = taskImagesLocalDataSource.write(image)
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
                    reminderDateTime = local.reminderDateTime?.toEpochMilliseconds(),
                    dueDateTime = local.dueDateTime?.toEpochMilliseconds(),
                    description = local.description,
                    wasArchived = local.isArchived
                )

                if (remote == null || local.updatedAt > remote.updatedAt) {
                    if (local.imageUri != null) {
                        val uri = taskImagesRemoteDataSource.uploadPicture(
                            taskImagesLocalDataSource.read(local.imageUri)
                        )
                        updatedRemote = updatedRemote.copy(path = uri)
                    }
                    tasksRemoteDataSource.save(updatedRemote, accountRepository.getUserId()!!)
                    tasksLocalDataSource.markAsSyncedIfUnchanged(local.id, local.updatedAt)
                }
            }

            localMap.values.forEach { local ->
                val remote = remoteMap[local.id]

                if (remote == null && !local.needsSync) {
                    tasksLocalDataSource.delete(local)
                }
            }

            tasksLocalDataSource.getTasks().first().forEach { task ->
                val taskBeforeSync = localTasks.firstOrNull {task.id == it.id}
                if (task.reminderDateTime != null &&
                    task.reminderDateTime != taskBeforeSync?.reminderDateTime) {
                        alarmScheduler.setReminder(
                            task.reminderDateTime.toEpochMilliseconds(),
                            task.id
                        )
                }
            }
        }
    }

    override suspend fun markAsDeleted(task: Task): Result<Unit> {
        return safeCall("markAsDeleted", TAG) {
            tasksLocalDataSource.markAsDeleted(task.id, System.currentTimeMillis())
            alarmScheduler.cancelReminder(task.id)
        }
    }

    override suspend fun getTaskById(id: String): Result<Task> {
        return safeCall("getTaskById", TAG) {
            tasksLocalDataSource.getTask(id)!!.asExternalModel()
        }
    }


    override suspend fun deleteAll(): Result<Unit> {
        return safeCall("deleteAll", TAG) {
            tasksLocalDataSource.deleteAll()
        }
    }
}
package com.example.donemate.data.sync

import com.example.donemate.data.local.TasksDao
import com.example.donemate.data.local.models.asEntity
import com.example.donemate.data.local.models.asExternalModel
import com.example.donemate.domain.model.Task
import com.example.donemate.data.remote.models.NetworkTask
import com.example.donemate.data.remote.models.asEntity
import com.example.donemate.domain.local.ImageStorage
import com.example.donemate.domain.remote.AccountRepository
import com.example.donemate.domain.remote.FirestoreRepository
import com.example.donemate.domain.remote.StorageRepository
import com.example.donemate.domain.sync.OfflineFirstTasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class OfflineFirstTasksRepositoryImpl @Inject constructor(private val storageRepository: StorageRepository,
                                                          private val firestoreRepository: FirestoreRepository,
                                                          private val accountRepository: AccountRepository,
                                                          private val tasksDao: TasksDao,
                                                          private val imageStorage: ImageStorage
): OfflineFirstTasksRepository {
     override suspend fun insert(task: Task){
         tasksDao.insert(task.asEntity(accountRepository.currentUserId))
    }


    override suspend fun delete(id: String){
        val task = tasksDao.getTask(id)
        task?.let { tasksDao.markAsDeleted(it.id) }
    }


    override suspend fun update(newTask: Task) {
        val oldTaskUri = tasksDao.getTask(newTask.id)?.imageUri
        val taskEntity = newTask.asEntity(accountRepository.currentUserId)
        if (oldTaskUri != taskEntity.imageUri && oldTaskUri != null) {
            deleteIfPrivateFile(oldTaskUri)
        }
        tasksDao.update(taskEntity)
    }

    override fun deleteIfPrivateFile(uri: String) {
        if (uri.isBlank()) return

        File(uri).delete()
    }

    override fun getTasks(): Flow<List<Task>> =
        tasksDao.getNotDeletedTasks()
            .map { entities ->
                entities.map { it.asExternalModel() }
            }

    override suspend fun sync() {
        val allLocal = tasksDao.getTasksIncludingDeleted()
        val needingSync = tasksDao.getTasksNeedingSync()
        val remoteTasks = firestoreRepository.getTasks()

        val localMap = allLocal.associateBy { it.id }
        val remoteMap = remoteTasks.associateBy { it.id }

        // Remote → Local
        remoteMap.values.forEach { remote ->
            val local = localMap[remote.id]


            if (local == null) {
                var taskEntity = remote.asEntity()
                remote.path?.let {
                    val image = storageRepository.downloadPicture(remote.path!!)
                    val uri = imageStorage.write(image)
                    taskEntity = taskEntity.copy(imageUri = uri)
                }
                tasksDao.insert(taskEntity)
            } else if (remote.updatedAt > local.updatedAt) {
                var taskEntity = remote.asEntity()
                remote.path?.let {
                    val image = storageRepository.downloadPicture(remote.path!!)
                    val uri = imageStorage.write(image)
                    taskEntity = taskEntity.copy(imageUri = uri)
                }
                tasksDao.update(taskEntity)
            }
        }

        // Local → Remote
        needingSync.forEach { local ->
            val remote = remoteMap[local.id]

            if (local.isDeleted) {
                firestoreRepository.delete(local.id)
                tasksDao.delete(local)
                return@forEach
            }

            var updatedRemote = NetworkTask(
                id = local.id,
                title = local.title,
                progress = local.progress,
                userId = local.userId,
                path = remote?.path,
                updatedAt = local.updatedAt
            )

            if (remote == null) {
                if (local.imageUri != null) {
                    val uri = storageRepository.uploadPicture(
                        local.id,
                        imageStorage.read(local.imageUri!!)
                    )
                    updatedRemote = updatedRemote.copy(path = uri)
                }
                firestoreRepository.save(updatedRemote)
                tasksDao.markUploaded(local.id)
            } else if (local.updatedAt > remote.updatedAt) {
                if (local.imageUri != null) {
                    val uri = storageRepository.uploadPicture(
                        local.id,
                        imageStorage.read(local.imageUri!!)
                    )
                    updatedRemote = updatedRemote.copy(path = uri)
                }
                firestoreRepository.update(updatedRemote)
                tasksDao.markUploaded(local.id)
            }
        }

        localMap.values.forEach { local ->
            val remote = remoteMap[local.id]

            if (remote == null && !local.needsSync) {

                tasksDao.delete(local)
            }
        }
    }

    override suspend fun markAsDeleted(task: Task) {
        tasksDao.markAsDeleted(task.id)
    }

    override suspend fun getTaskById(id: String): Task?{
        return tasksDao.getTask(id)?.asExternalModel()
    }
}


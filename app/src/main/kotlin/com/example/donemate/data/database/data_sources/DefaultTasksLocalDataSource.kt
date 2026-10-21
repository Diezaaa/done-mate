package com.example.donemate.data.database.data_sources

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.donemate.data.database.model.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

/*
 * Room generates the implementation of this DAO at compile time.
 * This DAO manages tasks in the local database.
 * It supports offline work by marking tasks that need to be synced later.
 */
@Dao
@Singleton
interface DefaultTasksLocalDataSource : TasksLocalDataSource {
    @Upsert
    override suspend fun upsert(taskEntity: TaskEntity)


    @Query(
        """
    UPDATE TaskEntity
    SET isDeleted = 1,
     needsSync = 1,
     updatedAt = :deletedAt
    WHERE id = :taskId
    """
    )
    override suspend fun markAsDeleted(
        taskId: String,
        deletedAt: Long
    )


    @Query(
        """
    DELETE FROM TaskEntity
    """
    )
    override suspend fun deleteAll()

    @Query(
        """
    SELECT * 
    FROM TaskEntity
    WHERE isDeleted = 0
    """
    )
    override fun getTasks(): Flow<List<TaskEntity>>

    @Query(
        """
    SELECT * 
    FROM TaskEntity 
    WHERE id = :id
    """
    )
    override suspend fun getTask(id: String): TaskEntity?

    @Query(
        """
        SELECT * 
        FROM TaskEntity
        WHERE needsSync = 1
    """
    )
    override suspend fun getTasksNeedingSync(): List<TaskEntity>

    @Query(
        """
        UPDATE TaskEntity
        SET needsSync = 0
        WHERE id = :id
          AND updatedAt = :expectedUpdatedAt
    """
    )
    override suspend fun markAsSyncedIfUnchanged(
        id: String,
        expectedUpdatedAt: Long,
    )

    @Query(
        """
    UPDATE TaskEntity
    SET isArchived = 1,
        needsSync = 1,
        updatedAt = :archivedAt
    WHERE id = :id
    """
    )
    override suspend fun markAsArchived(id: String, archivedAt: Long)

    @Query(
        """
        UPDATE TaskEntity
        SET isArchived = 0,
         needsSync = 1,
         updatedAt = :archivedAt
        WHERE id = :id"""
    )
    override suspend fun markAsUnArchived(id: String, archivedAt: Long)

    @Query(
        """
        SELECT * 
        FROM TaskEntity
        WHERE isDeleted = 0 AND isArchived = 0
    """
    )
    override fun getNotArchivedTasks(): Flow<List<TaskEntity>>


    @Query(
        """
        SELECT * 
        FROM TaskEntity
        WHERE isDeleted = 0 AND isArchived = 1
    """
    )
    override fun getArchivedTasks(): Flow<List<TaskEntity>>


    @Delete
    override suspend fun delete(taskEntity: TaskEntity)

    @Query(
        """
        SELECT * 
        FROM TaskEntity
    """
    )
    override suspend fun getTasksIncludingDeleted(): List<TaskEntity>
}
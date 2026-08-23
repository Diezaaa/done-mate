package com.example.donemate.data.database.data_sources

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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
interface DefaultTasksLocalDataSource: TasksLocalDataSource {
    @Upsert
    override suspend fun upsert(taskEntity: TaskEntity)



    @Query("""
    UPDATE TaskEntity
    SET isDeleted = 1,
     needsSync = 1
    WHERE id = :taskId
    """)
    override suspend fun markAsDeleted(taskId: String)

    @Query("""
    DELETE FROM TaskEntity
    """)
    override suspend fun deleteAll()


    @Query("""
    SELECT * 
    FROM TaskEntity 
    WHERE id = :id
""")
    override suspend fun getTask(id: String): TaskEntity?

    @Query(
        """
        SELECT * 
        FROM TaskEntity
        WHERE needsSync = 1
    """
    )
    override suspend fun getTasksNeedingSync(): List<TaskEntity>

    @Query("""
        UPDATE TaskEntity
        SET needsSync = 0
        WHERE id = :id
    """)
    override suspend fun markUploaded(id: String)

    @Query("""
        SELECT * 
        FROM TaskEntity
        WHERE isDeleted = 0
    """)
    override fun getNotDeletedTasks(): Flow<List<TaskEntity>>

    @Delete
    override suspend fun delete(taskEntity: TaskEntity)

    @Query("""
        SELECT * 
        FROM TaskEntity
    """)
    override suspend fun getTasksIncludingDeleted(): List<TaskEntity>
}
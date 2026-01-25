package com.example.donemate.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.donemate.data.local.models.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton


//
@Dao
@Singleton
interface TasksDao {
    @Insert
    suspend fun insert(taskEntity: TaskEntity)


    @Query("""
    UPDATE TaskEntity
    SET isDeleted = 1,
     needsSync = 1
    WHERE id = :taskId
    """)
    suspend fun markAsDeleted(taskId: String)

    @Update
    suspend fun update(taskEntity: TaskEntity)

    @Query("""
    SELECT * 
    FROM TaskEntity 
    WHERE id = :id
""")
    suspend fun getTask(id: String): TaskEntity?

    @Query(
        """
        SELECT * 
        FROM TaskEntity
        WHERE needsSync = 1
    """
    )
    suspend fun getTasksNeedingSync(): List<TaskEntity>

    @Query("""
        UPDATE TaskEntity
        SET needsSync = 0
        WHERE id = :id
    """)
    suspend fun markUploaded(id: String)

    @Query("""
        SELECT * 
        FROM TaskEntity
        WHERE isDeleted = 0
    """)
    fun getNotDeletedTasks(): Flow<List<TaskEntity>>

    @Delete
    suspend fun delete(taskEntity: TaskEntity)

    @Query("""
        SELECT * 
        FROM TaskEntity
    """)
    suspend fun getTasksIncludingDeleted(): List<TaskEntity>
}
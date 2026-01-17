package com.example.donemate.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.donemate.data.local.models.TaskPicture
import javax.inject.Singleton

@Dao
@Singleton
interface TaskPictureDao {
    @Insert
    suspend fun insert(taskPicture: TaskPicture)

    @Update
    suspend fun update(taskPicture: TaskPicture)

    @Delete
    suspend fun delete(taskPicture: TaskPicture)

    @Query("""
        UPDATE TaskPicture
        SET picture = :picture
        WHERE taskId = :taskId
    """)
    suspend fun updateTaskPictureUri(
        taskId: String,
        picture: String?
    )
    @Query("""
    SELECT picture
    FROM TaskPicture
    WHERE taskId = :taskId
    LIMIT 1
""")
    suspend fun getTaskPictureUri(taskId: String): String
}
package com.example.donemate.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase
import com.example.donemate.data.local.models.TaskEntity
import javax.inject.Singleton

@Singleton
@Database(entities = [TaskEntity::class],
    version = 5)
abstract class Database : RoomDatabase() {
    abstract fun taskPictureDao(): TasksDao
}
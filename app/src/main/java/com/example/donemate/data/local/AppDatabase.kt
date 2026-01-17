package com.example.donemate.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase
import com.example.donemate.data.local.models.TaskPicture;
import javax.inject.Singleton

@Singleton
@Database(entities = [TaskPicture::class], version = 2)
abstract class Database : RoomDatabase() {
    abstract fun taskPictureDao(): TaskPictureDao
}
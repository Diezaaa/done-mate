package com.example.donemate.data.local.db

import androidx.room.Database;
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.donemate.data.local.TasksDao
import com.example.donemate.data.local.model.QuoteEntity
import com.example.donemate.data.local.model.RemoteKey
import com.example.donemate.data.local.models.TaskEntity
import javax.inject.Singleton

@Singleton
@Database(entities = [TaskEntity::class,
    QuoteEntity::class,
    RemoteKey::class],
    version = 8)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun taskPictureDao(): TasksDao
    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun quotesDao(): QuotesDao
}
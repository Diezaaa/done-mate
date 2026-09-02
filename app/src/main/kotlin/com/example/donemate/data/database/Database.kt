package com.example.donemate.data.database

import androidx.room.Database;
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.donemate.data.database.data_sources.DefaultTasksLocalDataSource
import com.example.donemate.data.database.data_sources.DefaultQuotesLocalDataSource
import com.example.donemate.data.database.data_sources.DefaultRemoteKeyLocalDataSource
import com.example.donemate.data.database.model.QuoteEntity
import com.example.donemate.data.database.model.RemoteKeyEntity
import com.example.donemate.data.database.util.Converters
import com.example.donemate.data.database.model.TaskEntity
import javax.inject.Singleton

/*
 * Room is a persistence library that provides an abstraction layer over SQLite.
 * It lets the app store structured data locally using entities, DAOs, and a database class.
 */
// יישנו הסבר על הספריה גם בתיק פרויקט
@Singleton
@Database(entities = [TaskEntity::class,
    QuoteEntity::class,
    RemoteKeyEntity::class],
    version = 18)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    // DAO for task operations
    abstract fun defaultTasksLocalDataSource(): DefaultTasksLocalDataSource

    // DAO for paging remote keys
    abstract fun defaultRemoteKeyLocalDataSource(): DefaultRemoteKeyLocalDataSource

    // DAO for quote operations
    abstract fun defaultQuotesLocalDataSource(): DefaultQuotesLocalDataSource
}
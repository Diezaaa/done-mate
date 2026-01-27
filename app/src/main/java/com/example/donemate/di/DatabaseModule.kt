package com.example.donemate.di

import android.content.Context
import androidx.room.Room
import com.example.donemate.data.local.db.Database
import com.example.donemate.data.local.TasksDao
import com.example.donemate.data.local.db.QuotesDao
import com.example.donemate.data.local.db.RemoteKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(
            context,
            Database::class.java,
            "donemate_db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideTaskPictureDao(database: Database): TasksDao {
        return database.taskPictureDao()
    }

    @Provides
    fun provideRemoteKeyDao(database: Database): RemoteKeyDao {
        return database.remoteKeyDao()
    }

    @Provides
    fun provideQuotesDao(database: Database): QuotesDao {
        return database.quotesDao()
    }
}
package com.example.donemate.di

import com.example.donemate.data.database.Database
import com.example.donemate.data.database.data_sources.QuotesLocalDataSource
import com.example.donemate.data.database.data_sources.RemoteKeyLocalDataSource
import com.example.donemate.data.database.data_sources.TasksLocalDataSource
import com.example.donemate.data.datastore.DefaultPreferencesLocalDataSource
import com.example.donemate.data.datastore.PreferencesLocalDataSource
import com.example.donemate.data.remote.DefaultLikedQuoteRecordsRemoteDataSource
import com.example.donemate.data.remote.DefaultQuotesApi
import com.example.donemate.data.remote.DefaultTaskImagesRemoteDataSource
import com.example.donemate.data.remote.DefaultTasksRemoteDataSource
import com.example.donemate.data.remote.LikedQuoteRecordsRemoteDataSource
import com.example.donemate.data.remote.QuotesApi
import com.example.donemate.data.remote.TaskImagesRemoteDataSource
import com.example.donemate.data.remote.TasksRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourcesModule {

    @Binds
    abstract fun bindsPreferencesLocalDataSource(
        defaultPreferencesLocalDataSource: DefaultPreferencesLocalDataSource
    ): PreferencesLocalDataSource

    @Binds
    abstract fun bindsTaskImagesRemoteDataSource(
        defaultTaskImagesRemoteDataSource: DefaultTaskImagesRemoteDataSource
    ): TaskImagesRemoteDataSource

    @Binds
    abstract fun bindsTasksRemoteDataSource(
        defaultTasksRemoteDataSource: DefaultTasksRemoteDataSource
    ): TasksRemoteDataSource

    @Binds
    abstract fun bindsLikedQuoteRecordsRemoteDataSource(
        defaultLikedQuoteRecordsRemoteDataSource: DefaultLikedQuoteRecordsRemoteDataSource
    ): LikedQuoteRecordsRemoteDataSource
}


@Module
@InstallIn(SingletonComponent::class)
object DataSourcesProvidersModule {

    @Provides
    fun providesTasksLocalDataSource(
        database: Database
    ): TasksLocalDataSource {
        return database.defaultTasksLocalDataSource()
    }

    @Provides
    fun providesRemoteKeyLocalDataSource(
        database: Database
    ): RemoteKeyLocalDataSource {
        return database.defaultRemoteKeyLocalDataSource()
    }

    @Provides
    fun providesQuotesLocalDataSource(
        database: Database
    ): QuotesLocalDataSource {
        return database.defaultQuotesLocalDataSource()
    }
}
package com.example.donemate.di

import com.example.donemate.data.repository.local.DefaultPreferencesRepository
import com.example.donemate.data.repository.local.DefaultRemoteKeyRepository
import com.example.donemate.data.repository.offline_first.OfflineFirstQuotesRepository
import com.example.donemate.data.repository.offline_first.OfflineFirstTasksRepository
import com.example.donemate.data.repository.local.PreferencesRepository
import com.example.donemate.data.repository.local.RemoteKeyRepository
import com.example.donemate.data.repository.offline_first.QuotesRepository
import com.example.donemate.data.repository.offline_first.TasksRepository
import com.example.donemate.data.repository.remote.AccountRepository
import com.example.donemate.data.repository.remote.DefaultAccountRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindsTasksRepository(
        offlineFirstTasksRepository: OfflineFirstTasksRepository
    ): TasksRepository

    @Binds
    abstract fun bindsQuotesRepository(
        offlineFirstQuotesRepository: OfflineFirstQuotesRepository
    ): QuotesRepository


    @Binds
    abstract fun bindsPreferencesRepository(
        defaultPreferencesRepository: DefaultPreferencesRepository
    ): PreferencesRepository

    @Binds
    abstract fun bindsAccountRepository(
        defaultAccountRepository: DefaultAccountRepository
    ): AccountRepository

    @Binds
    abstract fun bindsRemoteKeyRepository(
        defaultRemoteKeyRepository: DefaultRemoteKeyRepository
    ): RemoteKeyRepository
}
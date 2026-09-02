package com.example.donemate.data.repository.local

import com.example.donemate.data.datastore.PreferencesLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultPreferencesRepository @Inject constructor(
    private val preferencesLocalDataSource: PreferencesLocalDataSource
): PreferencesRepository {
    override val isDarkTheme: Flow<Boolean> = preferencesLocalDataSource.isDarkTheme

    override suspend fun changeTheme(isDarkTheme: Boolean) {
        preferencesLocalDataSource.changeTheme(isDarkTheme)
    }
}
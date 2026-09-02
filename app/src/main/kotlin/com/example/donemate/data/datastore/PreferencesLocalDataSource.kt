package com.example.donemate.data.datastore

import kotlinx.coroutines.flow.Flow

interface PreferencesLocalDataSource {
    val isDarkTheme: Flow<Boolean>
    suspend fun changeTheme(isDarkTheme: Boolean)
}
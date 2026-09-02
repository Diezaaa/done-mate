package com.example.donemate.data.repository.local

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val isDarkTheme: Flow<Boolean>

    suspend fun changeTheme(isDarkTheme: Boolean)
}
package com.example.donemate.domain.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ThemeRepository{
    val isDarkTheme: Flow<Boolean>
    suspend fun changeTheme(isDarkTheme: Boolean)
}
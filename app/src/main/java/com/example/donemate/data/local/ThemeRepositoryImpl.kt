package com.example.donemate.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.donemate.domain.local.ThemeRepository

@Singleton
class ThemeRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): ThemeRepository{
    override val isDarkTheme: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[ISDARKTHEME] ?: false
        }
    override suspend fun changeTheme(isDarkTheme: Boolean){
        dataStore.edit{ preferences ->
            preferences[ISDARKTHEME] = isDarkTheme
        }

    }
    private companion object {
        val ISDARKTHEME = booleanPreferencesKey("isDarkTheme")

    }
}
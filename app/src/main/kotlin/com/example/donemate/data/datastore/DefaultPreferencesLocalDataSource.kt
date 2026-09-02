package com.example.donemate.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultPreferencesLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
): PreferencesLocalDataSource {
    // TODO: add file corruption handling, guide link: https://developer.android.com/topic/libraries/architecture/datastore#handle-file-corruption
    override val isDarkTheme: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                Log.e("DefaultPreferencesLocalDataSource", "Unexpected DataStore error", exception)
                throw exception
            }
        }.map { preferences ->
            preferences[IS_DARK_THEME] ?: false
        }

    override suspend fun changeTheme(isDarkTheme: Boolean){
        try {
            dataStore.edit { preferences ->
                preferences[IS_DARK_THEME] = isDarkTheme
            }
        }
        catch (e: IOException) {
            Log.e("DefaultPreferencesLocalDataSource", "Failed to change theme", e)
        }
    }

    private companion object {
        val IS_DARK_THEME = booleanPreferencesKey("isDarkTheme")
    }
}
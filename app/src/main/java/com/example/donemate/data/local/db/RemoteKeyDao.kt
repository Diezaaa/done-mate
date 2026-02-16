package com.example.donemate.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.donemate.data.local.model.RemoteKey

@Dao
interface RemoteKeyDao {
    @Query("SELECT * FROM remotekey")
    suspend fun getRemoteKey(): RemoteKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: RemoteKey)

}
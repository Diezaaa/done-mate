package com.example.donemate.data.database.data_sources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.donemate.data.database.model.RemoteKeyEntity

/*
 * Room generates the implementation of this DAO at compile time.
 * The app only defines the queries, and Room creates the actual database code.
 * This DAO is used by Paging 3 to save and read the current remote paging key.
 */
@Dao
interface DefaultRemoteKeyLocalDataSource : RemoteKeyLocalDataSource {
    @Query("SELECT * FROM remotekeyentity")
    override suspend fun getRemoteKey(): RemoteKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertOrReplace(remoteKeyEntity: RemoteKeyEntity)
}
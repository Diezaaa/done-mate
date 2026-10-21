package com.example.donemate.data.database.data_sources

import com.example.donemate.data.database.model.RemoteKeyEntity

interface RemoteKeyLocalDataSource {
    suspend fun insertOrReplace(remoteKeyEntity: RemoteKeyEntity)
    suspend fun getRemoteKey(): RemoteKeyEntity?
}
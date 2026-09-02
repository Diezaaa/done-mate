package com.example.donemate.data.repository.local

import com.example.donemate.data.common.Result
import com.example.donemate.data.database.model.RemoteKeyEntity

interface RemoteKeyRepository {
    suspend fun getRemoteKey(): Result<RemoteKeyEntity?>

    suspend fun insertOrReplace(remoteKeyEntity: RemoteKeyEntity): Result<Unit>
}
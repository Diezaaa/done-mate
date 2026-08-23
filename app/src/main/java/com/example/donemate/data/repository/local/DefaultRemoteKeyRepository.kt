package com.example.donemate.data.repository.local

import com.example.donemate.data.common.Result
import com.example.donemate.data.common.safeCall
import com.example.donemate.data.database.data_sources.RemoteKeyLocalDataSource
import com.example.donemate.data.database.model.RemoteKeyEntity
import javax.inject.Inject

private const val TAG = "DefaultRemoteKeyRepository"

class DefaultRemoteKeyRepository @Inject constructor(
    private val remoteKeyLocalDataSource: RemoteKeyLocalDataSource
) : RemoteKeyRepository {
    override suspend fun getRemoteKey(): Result<RemoteKeyEntity?> =
        safeCall("getRemoteKey", TAG) {
            remoteKeyLocalDataSource.getRemoteKey()
        }


    override suspend fun insertOrReplace(remoteKeyEntity: RemoteKeyEntity) =
        safeCall("insertOrReplace", TAG) {
            remoteKeyLocalDataSource.insertOrReplace(remoteKeyEntity)
        }
}
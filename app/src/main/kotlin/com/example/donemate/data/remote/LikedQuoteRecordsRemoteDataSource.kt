package com.example.donemate.data.remote

import com.example.donemate.data.remote.model.RemoteLikedQuoteRecord

interface LikedQuoteRecordsRemoteDataSource {
    suspend fun getLikedQuoteRecords(userId: String): List<RemoteLikedQuoteRecord>
    suspend fun save(remoteLikedQuoteRecord: RemoteLikedQuoteRecord)
    suspend fun delete(quoteId: String, userId: String)
}
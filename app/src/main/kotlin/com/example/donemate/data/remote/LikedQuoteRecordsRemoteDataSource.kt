package com.example.donemate.data.remote

import com.example.donemate.data.remote.model.RemoteLikedQuoteRecord

interface LikedQuoteRecordsRemoteDataSource {
    suspend fun getLikedQuoteRecords(userId: String): List<RemoteLikedQuoteRecord>
    suspend fun saveLikedQuoteRecord(remoteLikedQuoteRecord: RemoteLikedQuoteRecord)
    suspend fun updateLikedQuoteRecord(remoteLikedQuoteRecord: RemoteLikedQuoteRecord)
    suspend fun deleteLikedQuoteRecord(remoteLikedQuoteRecord: RemoteLikedQuoteRecord)
}
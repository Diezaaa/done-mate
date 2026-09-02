package com.example.donemate.data.remote

import com.example.donemate.data.remote.model.RemoteLikedQuoteRecord
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class DefaultLikedQuoteRecordsRemoteDataSource @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : LikedQuoteRecordsRemoteDataSource {
    override suspend fun getLikedQuoteRecords(userId: String): List<RemoteLikedQuoteRecord> {
        val snap = firebaseFirestore.collection("liked_quote_records")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return snap.toObjects(RemoteLikedQuoteRecord::class.java)
    }

    override suspend fun saveLikedQuoteRecord(
        remoteLikedQuoteRecord: RemoteLikedQuoteRecord
    ) {
        val docRef =
            firebaseFirestore.collection("liked_quote_records").document(remoteLikedQuoteRecord.id)

        docRef.set(remoteLikedQuoteRecord)
    }

    override suspend fun updateLikedQuoteRecord(
        remoteLikedQuoteRecord: RemoteLikedQuoteRecord) {
        firebaseFirestore.collection("liked_quote_records")
            .document(remoteLikedQuoteRecord.id)
            .set(remoteLikedQuoteRecord)
            .await()
    }

    override suspend fun deleteLikedQuoteRecord(
        remoteLikedQuoteRecord: RemoteLikedQuoteRecord) {
        firebaseFirestore.collection("liked_quote_records").document(remoteLikedQuoteRecord.id)
            .delete()
            .await()
    }
}
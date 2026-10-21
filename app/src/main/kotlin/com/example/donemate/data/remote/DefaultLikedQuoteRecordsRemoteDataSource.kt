package com.example.donemate.data.remote

import com.example.donemate.data.remote.model.RemoteLikedQuoteRecord
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class DefaultLikedQuoteRecordsRemoteDataSource @Inject constructor(
    firebaseFirestore: FirebaseFirestore
) : LikedQuoteRecordsRemoteDataSource {
    private val collectionReference = firebaseFirestore.collection("liked_quote_records")

    override suspend fun getLikedQuoteRecords(userId: String): List<RemoteLikedQuoteRecord> {
        val query = collectionReference
            .whereEqualTo("userId", userId)

        val snap = ServerReads.get(query).await()

        return snap.toObjects(RemoteLikedQuoteRecord::class.java)
    }

    override suspend fun save(
        remoteLikedQuoteRecord: RemoteLikedQuoteRecord
    ) {
        val docRef =
            collectionReference.document(remoteLikedQuoteRecord.id)

        docRef.set(remoteLikedQuoteRecord)
    }

    override suspend fun delete(quoteId: String, userId: String) {
        val quotesWithQuoteIdQuery = collectionReference.whereEqualTo("quoteId", quoteId)
            .whereEqualTo("userId", userId)

        val snap = ServerReads.get(quotesWithQuoteIdQuery).await()

        snap.forEach {
            it.reference.delete()
        }
    }
}




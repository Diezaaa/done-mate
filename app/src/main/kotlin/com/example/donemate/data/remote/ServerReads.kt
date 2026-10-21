package com.example.donemate.data.remote

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await

/**
 * Provides Firestore reads that require a server response.
 *
 * Read failures are reported through the returned task without falling back to
 * cached data.
 */
object ServerReads {

    /**
     * Executes [query] using [Source.SERVER].
     *
     * @param query The Firestore query to execute.
     * @return A task containing the query snapshot, which may contain no documents.
     * The task fails if the server cannot be reached.
     */
    fun get(query: Query) = query.get(Source.SERVER)

    /**
     * Fetches [document] using [Source.SERVER].
     *
     * @param document A reference to the Firestore document to fetch.
     * @return A task containing the document snapshot. If the document does not
     * exist, the snapshot's `exists()` returns `false`.
     * The task fails if the server cannot be reached.
     */
    fun get(document: DocumentReference) =
        document.get(Source.SERVER)
}
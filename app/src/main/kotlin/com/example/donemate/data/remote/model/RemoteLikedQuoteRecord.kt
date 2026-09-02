package com.example.donemate.data.remote.model

import com.google.firebase.firestore.DocumentId

// TODO: Firestore requires me to provide initial values. However I don't want to. I think it messes up the code a bit
data class RemoteLikedQuoteRecord (
    @DocumentId val id: String = "",
    val updatedAt: Long = 0,
    val userId: String = ""
)
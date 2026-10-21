package com.example.donemate.data.model

import com.example.donemate.data.database.model.QuoteEntity
import com.example.donemate.data.remote.model.RemoteQuote

data class Quote(
    val id: String,
    val content: String,
    val author: String,
    val isLiked: Boolean
)

fun RemoteQuote.asEntity() = QuoteEntity(
    id = id,
    content = content,
    author = author,
    isLiked = false,
    updatedAt = null,
    needsSync = false
)

fun QuoteEntity.asExternalModel() = Quote(
    id = id,
    content = content,
    author = author,
    isLiked = isLiked
)


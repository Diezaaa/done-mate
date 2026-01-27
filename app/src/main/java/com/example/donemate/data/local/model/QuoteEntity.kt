package com.example.donemate.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.donemate.domain.model.Quote

@Entity
data class QuoteEntity(
    @PrimaryKey val id: String,
    val content: String,
    val author: String
)

fun QuoteEntity.asExternalModel() = Quote(
    id = id,
    content = content,
    author = author
)
package com.example.donemate.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuoteEntity(
    @PrimaryKey val id: String,
    val content: String,
    val author: String,
    val isLiked: Boolean,
    val updatedAt: Long?,
    val needsSync: Boolean
)
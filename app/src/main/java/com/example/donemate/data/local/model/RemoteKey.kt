package com.example.donemate.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RemoteKey(
    @PrimaryKey val id: Int,
    val current: Int, val nextKey: Int?
)
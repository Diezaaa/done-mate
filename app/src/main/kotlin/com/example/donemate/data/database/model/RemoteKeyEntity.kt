package com.example.donemate.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RemoteKeyEntity(
    @PrimaryKey val id: Int,
    val current: Int,
    val nextKey: Int?
)
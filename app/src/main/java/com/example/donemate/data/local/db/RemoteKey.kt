package com.example.donemate.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RemoteKey(
    @PrimaryKey  val id: Int,
    val current: Int, val nextKey: Int?)
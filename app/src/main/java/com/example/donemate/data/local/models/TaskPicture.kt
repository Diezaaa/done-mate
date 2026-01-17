package com.example.donemate.data.local.models

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskPicture(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val taskId: String = "",
    val picture: String
)
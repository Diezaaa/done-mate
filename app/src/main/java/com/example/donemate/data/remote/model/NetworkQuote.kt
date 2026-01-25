package com.example.donemate.data.remote.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkQuote (
    @SerializedName("id")
     val id: String,
     val content: String,
     val author: String,
     val authorSlug: String,
     val length: Int,
     val tags: List<String>
)

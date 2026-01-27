package com.example.donemate.data.remote.model

import com.example.donemate.data.local.model.QuoteEntity
import com.example.donemate.domain.model.Quote
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkQuote (
    @SerializedName("_id")
     val id: String,
     val content: String,
     val author: String,
     val authorSlug: String,
     val length: Int,
     val tags: List<String>
)

fun NetworkQuote.asEntity() = QuoteEntity(
    id = id,
    content = content,
    author = author
)
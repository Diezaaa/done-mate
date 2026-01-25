package com.example.donemate.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkQuotesList(
    val count: Int,
    val totalCount: Int,
    val page: Int,
    val totalPages: Int,
    val lastItemIndex: Int,
    val results: List<NetworkQuote>
)
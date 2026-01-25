package com.example.donemate.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.donemate.data.remote.QuoteApi
import com.example.donemate.data.remote.model.NetworkQuote
import javax.inject.Inject

class QuotesPagingSource
@Inject constructor(
    val api: QuoteApi
) : PagingSource<Int, NetworkQuote>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, NetworkQuote> {
        try {
            val nextPageNumber = params.key ?: 1
            val response = api.getQuotes(nextPageNumber)
            Log.d("Paging", "Loaded ${response.results.size} items for page=${params.key}")

            return LoadResult.Page(
                data = response.results,
                prevKey = null,
                nextKey = nextPageNumber + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, NetworkQuote>): Int? {

        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
package com.example.donemate.ui.screens.Inspiration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.donemate.data.paging.QuotesPagingSource
import com.example.donemate.data.remote.QuoteApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// What does () in the ViewModel() mean?
@HiltViewModel
class InspirationViewModel @Inject constructor(
    private val api: QuoteApi
) : ViewModel() {
    val flow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 20)
    ) {
        QuotesPagingSource(api)
    }.flow
        .cachedIn(viewModelScope)

}
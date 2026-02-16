package com.example.donemate.ui.screens.inspiration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.map
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.donemate.data.local.db.QuotesDao
import com.example.donemate.data.local.db.RemoteKeyDao
import com.example.donemate.data.local.model.QuoteEntity
import com.example.donemate.data.local.model.asExternalModel
import com.example.donemate.data.paging.InspirationRemoteMediator
import com.example.donemate.domain.remote.QuotesRepository
import com.example.donemate.domain.model.Quote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// What does () in the ViewModel() mean?
@HiltViewModel
class InspirationViewModel @Inject constructor(
    private val quotesDao: QuotesDao,
    private val remoteKeyDao: RemoteKeyDao,
    private val quotesRepository: QuotesRepository
) : ViewModel() {
    @OptIn(ExperimentalPagingApi::class)
    val flow = Pager(
        config = PagingConfig(pageSize = 20,
            initialLoadSize = 20,
            prefetchDistance = 0),
        remoteMediator = InspirationRemoteMediator(
            quotesDao = quotesDao,
            remoteKeyDao = remoteKeyDao,
            quotesRepository = quotesRepository
        )
    ) {
        quotesDao.pagingSource()
    }
    .flow
    .cachedIn(viewModelScope)

}
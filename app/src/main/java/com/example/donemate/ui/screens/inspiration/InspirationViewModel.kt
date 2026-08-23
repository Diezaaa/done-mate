package com.example.donemate.ui.screens.inspiration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.donemate.data.paging.InspirationRemoteMediator
import com.example.donemate.data.repository.offline_first.QuotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import androidx.paging.map
import com.example.donemate.data.model.asExternalModel
import com.example.donemate.data.repository.local.RemoteKeyRepository

@HiltViewModel
class InspirationViewModel @Inject constructor(
    private val quotesRepository: QuotesRepository,
    private val remoteKeyRepository: RemoteKeyRepository,
) : ViewModel() {
    @OptIn(ExperimentalPagingApi::class)
    val flow = Pager(
        config = PagingConfig(pageSize = 20,
            initialLoadSize = 20,
            prefetchDistance = 0),
        remoteMediator = InspirationRemoteMediator(
            quotesRepository = quotesRepository,
            remoteKeyRepository = remoteKeyRepository
        )
    ) {
        quotesRepository.getQuotesPagingSource()
    }
    .flow
    .map { pagingData ->
        pagingData.map { quoteEntity -> quoteEntity.asExternalModel() }
    }
    .cachedIn(viewModelScope)

}